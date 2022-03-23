package com.dynxsty.dih4jda;

import com.dynxsty.dih4jda.commands.interactions.context_command.IMessageContextCommand;
import com.dynxsty.dih4jda.commands.interactions.context_command.IUserContextCommand;
import com.dynxsty.dih4jda.commands.interactions.context_command.MessageContextInteraction;
import com.dynxsty.dih4jda.commands.interactions.context_command.UserContextInteraction;
import com.dynxsty.dih4jda.commands.interactions.context_command.dao.BaseContextCommand;
import com.dynxsty.dih4jda.commands.interactions.context_command.dao.GlobalContextCommand;
import com.dynxsty.dih4jda.commands.interactions.context_command.dao.GuildContextCommand;
import com.dynxsty.dih4jda.commands.interactions.slash_command.ISlashCommand;
import com.dynxsty.dih4jda.commands.interactions.slash_command.SlashCommandInteraction;
import com.dynxsty.dih4jda.commands.interactions.slash_command.dao.*;
import com.dynxsty.dih4jda.exceptions.CommandNotRegisteredException;
import com.dynxsty.dih4jda.util.CommandUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class InteractionHandler extends ListenerAdapter {

	private final DIH4JDA dih4jda;
	private final Map<String, SlashCommandInteraction> slashCommandIndex;
	private final Map<String, MessageContextInteraction> messageContextIndex;
	private final Map<String, UserContextInteraction> userContextIndex;

	private final Set<Class<? extends BaseSlashCommand>> guildCommands;
	private final Set<Class<? extends BaseSlashCommand>> globalCommands;

	private final Set<Class<? extends BaseContextCommand>> guildContexts;
	private final Set<Class<? extends BaseContextCommand>> globalContexts;

	/**
	 * Constructs a new {@link InteractionHandler} from the supplied commands package.
	 *
	 * @param dih4jda The {@link DIH4JDA} instance.
	 */
	protected InteractionHandler(DIH4JDA dih4jda) {
		this.guildCommands = new HashSet<>();
		this.globalCommands = new HashSet<>();
		this.guildContexts = new HashSet<>();
		this.globalContexts = new HashSet<>();
		this.slashCommandIndex = new HashMap<>();
		this.messageContextIndex = new HashMap<>();
		this.userContextIndex = new HashMap<>();
		this.dih4jda = dih4jda;
	}

	public void registerInteractions(JDA jda) throws Exception {
		this.registerSlashCommands();
		this.registerContextCommands();
		// register commands for each guild
		for (Guild guild : jda.getGuilds()) {
			Set<CommandData> commands = new HashSet<>();
			commands.addAll(this.getGuildSlashCommandData(guild));
			commands.addAll(this.getGuildContextCommandData(guild));
			guild.updateCommands().addCommands(commands).queue();
			DIH4JDALogger.info(String.format("Queued %s command(s) in guild %s: %s", commands.size(), guild.getName(), commands.stream().map(CommandData::getName).collect(Collectors.joining(", "))), DIH4JDALogger.Type.COMMANDS_QUEUED);
		}
		final List<Command> existingData = jda.retrieveCommands().complete();
		List<Command> allCommands = new ArrayList<>(existingData);
		Set<SlashCommandData> slashData = new HashSet<>(this.getGlobalSlashCommandData());
		Set<CommandData> commandData = new HashSet<>(this.getGlobalContextCommandData());
		// check if smart queuing was disabled
		if (this.dih4jda.isSmartQueuing() && allCommands.size() > 0) {
			DIH4JDALogger.info(String.format("Found %s existing global command(s). Trying to just queue edited commands...", allCommands.size()), DIH4JDALogger.Type.SMART_QUEUE);
			commandData.removeIf(command -> existingData.stream().anyMatch(data -> this.isCommandData(allCommands, data, command)));
			slashData.removeIf(command -> existingData.stream().anyMatch(data -> this.isCommandData(allCommands, data, command)));
			// remove unknown commands
			if (allCommands.size() > 0) {
				DIH4JDALogger.info(String.format("Found %s unknown command(s). Attempting deletion.", allCommands.size()), DIH4JDALogger.Type.SMART_QUEUE);
				for (Command command : allCommands) {
					DIH4JDALogger.info(String.format("Deleting unknown %s command: %s", command.getType(), command.getName()), DIH4JDALogger.Type.SMART_QUEUE);
					jda.deleteCommandById(command.getId()).queue();
				}
			}
		}
		commandData.addAll(slashData);
		// queue all global commands
		if (commandData.size() > 0) {
			jda.updateCommands().addCommands(commandData).queue();
			DIH4JDALogger.info(String.format("Queued %s global command(s): %s", commandData.size(), commandData.stream().map(CommandData::getName).collect(Collectors.joining(", "))), DIH4JDALogger.Type.COMMANDS_QUEUED);
		}
		// register command privileges
		this.registerCommandPrivileges(jda);
	}

	private boolean isCommandData(List<Command> allCommands, Command command, Object data) {
		boolean equals = false;
		if (data instanceof CommandData) equals = CommandUtils.equals((CommandData) data, command);
		if (data instanceof SlashCommandData) equals = CommandUtils.equals((SlashCommandData) data, command);
		if (equals) {
			allCommands.remove(command);
			DIH4JDALogger.info(String.format("Found duplicate %s command, which will be ignored: %s", command.getType(), command.getName()), DIH4JDALogger.Type.SMART_QUEUE);
		}
		return equals;
	}

	/**
	 * Registers all slash commands. Loops through all classes found in the commands package that is a subclass of {@link BaseSlashCommand}.
	 * Goes through these steps with every iteration;
	 * <ol>
	 *     <li>Checks if the class is missing {@link CommandData} and doesn't register if it is.</li>
	 *     <li>Checks if the class is neither a subclass of {@link Subcommand} nor {@link SubcommandGroup} and registers it as regular command.</li>
	 *     <li>Checks if the class is a subclass of {@link SubcommandGroup} if it is, the SlashCommandGroup is validated and another loop is fired following the two steps above for the group's sub commands.</li>
	 *     <li>Checks if the class is a subclass of {@link Subcommand}, if it is, it is registered as a sub command.</li>
	 * </ol>
	 */
	private void registerSlashCommands() {
		Reflections commands = new Reflections(this.dih4jda.getCommandsPackage());
		Set<Class<? extends BaseSlashCommand>> classes = commands.getSubTypesOf(BaseSlashCommand.class);
		for (Class<? extends BaseSlashCommand> c : classes) {
			if (c.getSuperclass().equals(GlobalSlashCommand.class)) {
				globalCommands.add(c);
			} else if (c.getSuperclass().equals(GuildSlashCommand.class)) {
				guildCommands.add(c);
			}
		}
	}

	/**
	 * Registers all context commands. Loops through all classes found in the commands package that is a subclass of {@link BaseContextCommand}.
	 */
	private void registerContextCommands() {
		Reflections commands = new Reflections(this.dih4jda.getCommandsPackage());
		Set<Class<? extends BaseContextCommand>> classes = commands.getSubTypesOf(BaseContextCommand.class);
		for (Class<? extends BaseContextCommand> c : classes) {
			if (c.getSuperclass().equals(GlobalContextCommand.class)) {
				globalContexts.add(c);
			} else if (c.getSuperclass().equals(GuildContextCommand.class)) {
				guildContexts.add(c);
			}
		}
	}

	/**
	 * Registers all Command Privileges.
	 *
	 * @param jda The {@link JDA} instance.
	 */
	private void registerCommandPrivileges(JDA jda) {
		for (Guild guild : jda.getGuilds()) {
			Map<String, Set<CommandPrivilege>> privileges = new HashMap<>();
			guild.retrieveCommands().queue(commands -> {
				for (Command command : commands) {
					if (privileges.containsKey(command.getId())) continue;
					Optional<SlashCommandInteraction> interactionOptional = this.slashCommandIndex
							.keySet()
							.stream()
							.filter(p -> p.equals(command.getName()) || p.split("/")[0].equals(command.getName()))
							.map(slashCommandIndex::get)
							.filter(p -> p.getPrivileges() != null && p.getPrivileges().length > 0)
							.findFirst();
					if (interactionOptional.isPresent()) {
						SlashCommandInteraction interaction = interactionOptional.get();
						if (interaction.getBaseClass().getSuperclass().equals(GlobalSlashCommand.class)) {
							DIH4JDALogger.error(String.format("Can not register command privileges for global command %s (%s).", command.getName(), interaction.getBaseClass().getSimpleName()));
							continue;
						}
						privileges.put(command.getId(), new HashSet<>(Arrays.asList(interaction.getPrivileges())));
						DIH4JDALogger.info(String.format("[%s] Registered privileges for command %s: %s", guild.getName(), command.getName(), Arrays.stream(interaction.getPrivileges()).map(CommandPrivilege::toData).collect(Collectors.toList())), DIH4JDALogger.Type.COMMAND_PRIVILEGE_REGISTERED);
					}
					if (privileges.isEmpty()) continue;
					guild.updateCommandPrivileges(privileges).queue();
				}
			});
		}
	}

	/**
	 * Gets all Guild commands registered in {@link InteractionHandler#registerSlashCommands()} and adds
	 * them to the {@link InteractionHandler#slashCommandIndex}.
	 *
	 * @param guild The command's guild.
	 * @throws Exception If an error occurs.
	 */
	private Set<SlashCommandData> getGuildSlashCommandData(@NotNull Guild guild) throws Exception {
		Set<SlashCommandData> commands = new HashSet<>();
		for (Class<? extends BaseSlashCommand> slashCommandClass : this.guildCommands) {
			BaseSlashCommand instance = (BaseSlashCommand) this.getClassInstance(guild, slashCommandClass);
			commands.add(this.getBaseCommandData(instance, slashCommandClass, guild));
		}
		return commands;
	}

	/**
	 * Gets all Global commands registered in {@link InteractionHandler#registerSlashCommands()} and adds
	 * them to the {@link InteractionHandler#slashCommandIndex}.
	 *
	 * @throws Exception If an error occurs.
	 */
	private Set<SlashCommandData> getGlobalSlashCommandData() throws Exception {
		Set<SlashCommandData> commands = new HashSet<>();
		for (Class<? extends BaseSlashCommand> slashCommandClass : this.globalCommands) {
			BaseSlashCommand instance = (BaseSlashCommand) this.getClassInstance(null, slashCommandClass);
			commands.add(this.getBaseCommandData(instance, slashCommandClass, null));
		}
		return commands;
	}

	/**
	 * Gets the complete {@link SlashCommandData} (including Subcommands & Subcommand Groups) of a single {@link BaseSlashCommand}.
	 *
	 * @param command      The base command's instance.
	 * @param commandClass The base command's class.
	 * @param guild        The current guild (if available)
	 * @return The new {@link CommandListUpdateAction}.
	 * @throws Exception If an error occurs.
	 */
	private SlashCommandData getBaseCommandData(@NotNull BaseSlashCommand command, Class<? extends BaseSlashCommand> commandClass, @Nullable Guild guild) throws Exception {
		if (command.getCommandData() == null) {
			DIH4JDALogger.warn(String.format("Class %s is missing CommandData. It will be ignored.", commandClass.getName()));
			return null;
		}
		SlashCommandData commandData = command.getCommandData();
		if (command.getSubcommandGroups() != null) {
			commandData.addSubcommandGroups(this.getSubcommandGroupData(command, guild));
		}
		if (command.getSubcommands() != null) {
			commandData.addSubcommands(this.getSubcommandData(command, command.getSubcommands(), null, guild));
		}
		if (command.getSubcommandGroups() == null && command.getSubcommands() == null) {
			slashCommandIndex.put(buildCommandPath(commandData.getName()), new SlashCommandInteraction((ISlashCommand) command, commandClass, command.getCommandPrivileges()));
			DIH4JDALogger.info(String.format("\t[*] Registered command: /%s", command.getCommandData().getName()), DIH4JDALogger.Type.SLASH_COMMAND_REGISTERED);
		}
		return commandData;
	}

	/**
	 * Gets all {@link SubcommandGroupData} (including Subcommands) of a single {@link BaseSlashCommand}.
	 *
	 * @param command The base command's instance.
	 * @param guild   The current guild (if available)
	 * @return All {@link SubcommandGroupData} stored in a List.
	 * @throws Exception If an error occurs.
	 */
	private Set<SubcommandGroupData> getSubcommandGroupData(@NotNull BaseSlashCommand command, @Nullable Guild guild) throws Exception {
		Set<SubcommandGroupData> groupDataList = new HashSet<>();
		for (Class<? extends SubcommandGroup> group : command.getSubcommandGroups()) {
			SubcommandGroup instance = (SubcommandGroup) this.getClassInstance(guild, group);
			if (instance.getSubcommandGroupData() == null) {
				DIH4JDALogger.warn(String.format("Class %s is missing SubcommandGroupData. It will be ignored.", group.getName()));
				continue;
			}
			if (instance.getSubcommands() == null) {
				DIH4JDALogger.warn(String.format("SubcommandGroup %s is missing Subcommands. It will be ignored.", instance.getSubcommandGroupData().getName()));
				continue;
			}
			SubcommandGroupData groupData = instance.getSubcommandGroupData();
			groupData.addSubcommands(this.getSubcommandData(command, instance.getSubcommands(), groupData.getName(), guild));
			groupDataList.add(groupData);
		}
		return groupDataList;
	}

	/**
	 * Gets all {@link SubcommandData} from the given array of {@link Subcommand} classes.
	 *
	 * @param command      The base command's instance.
	 * @param subClasses   All sub command classes.
	 * @param subGroupName The Subcommand Group's name. (if available)
	 * @param guild        The current guild (if available)
	 * @return The new {@link CommandListUpdateAction}.
	 * @throws Exception If an error occurs.
	 */
	private Set<SubcommandData> getSubcommandData(BaseSlashCommand command, Class<? extends Subcommand>[] subClasses, @Nullable String subGroupName, @Nullable Guild guild) throws Exception {
		Set<SubcommandData> subDataList = new HashSet<>();
		for (Class<? extends Subcommand> sub : subClasses) {
			Subcommand instance = (Subcommand) this.getClassInstance(guild, sub);
			if (instance.getSubcommandData() == null) {
				DIH4JDALogger.warn(String.format("Class %s is missing SubcommandData. It will be ignored.", sub.getName()));
				continue;
			}
			String commandPath;
			if (subGroupName == null) {
				commandPath = buildCommandPath(command.getCommandData().getName(), instance.getSubcommandData().getName());
			} else {
				commandPath = buildCommandPath(command.getCommandData().getName(), subGroupName, instance.getSubcommandData().getName());
			}
			slashCommandIndex.put(commandPath, new SlashCommandInteraction((ISlashCommand) instance, sub, command.getCommandPrivileges()));
			DIH4JDALogger.info(String.format("\t[*] Registered command: /%s", commandPath), DIH4JDALogger.Type.SLASH_COMMAND_REGISTERED);
			subDataList.add(instance.getSubcommandData());
		}
		return subDataList;
	}

	/**
	 * Gets all Guild Context commands registered in {@link InteractionHandler#registerContextCommands()} and
	 * returns their {@link CommandData} as a List.
	 *
	 * @param guild The context command's guild.
	 * @throws Exception If an error occurs.
	 */
	private Set<CommandData> getGuildContextCommandData(@NotNull Guild guild) throws Exception {
		Set<CommandData> commands = new HashSet<>();
		for (Class<? extends BaseContextCommand> contextCommandClass : this.guildContexts) {
			BaseContextCommand instance = (BaseContextCommand) this.getClassInstance(guild, contextCommandClass);
			commands.add(this.getContextCommandData(instance, contextCommandClass));
		}
		return commands;
	}

	/**
	 * Gets all Global Context commands registered in {@link InteractionHandler#registerContextCommands()} and
	 * returns their {@link CommandData} as a List.
	 *
	 * @throws Exception If an error occurs.
	 */
	private Set<CommandData> getGlobalContextCommandData() throws Exception {
		Set<CommandData> commands = new HashSet<>();
		for (Class<? extends BaseContextCommand> contextCommandClass : this.globalContexts) {
			BaseContextCommand instance = (BaseContextCommand) this.getClassInstance(null, contextCommandClass);
			CommandData data = this.getContextCommandData(instance, contextCommandClass);
			if (data != null) {
				commands.add(data);
			}
		}
		return commands;
	}

	/**
	 * Gets the complete {@link CommandData} from a single {@link BaseContextCommand}.
	 *
	 * @param command      The base context command's instance.
	 * @param commandClass The base context command's class.
	 * @return The new {@link CommandListUpdateAction}.
	 */
	private CommandData getContextCommandData(@NotNull BaseContextCommand command, Class<? extends BaseContextCommand> commandClass) {
		if (command.getCommandData() == null) {
			DIH4JDALogger.warn(String.format("Class %s is missing CommandData. It will be ignored.", commandClass.getName()));
			return null;
		}
		CommandData commandData = command.getCommandData();
		if (commandData.getType() == Command.Type.MESSAGE) {
			messageContextIndex.put(commandData.getName(), new MessageContextInteraction((IMessageContextCommand) command));
		} else if (commandData.getType() == Command.Type.USER) {
			userContextIndex.put(commandData.getName(), new UserContextInteraction((IUserContextCommand) command));
		} else {
			DIH4JDALogger.error(String.format("Invalid Command Type \"%s\" for Context Command! This command will be ignored.", commandData.getType()));
			return null;
		}
		DIH4JDALogger.info(String.format("\t[*] Registered context command: %s", command.getCommandData().getName()), DIH4JDALogger.Type.CONTEXT_COMMAND_REGISTERED);
		return commandData;
	}

	/**
	 * Handles a single {@link SlashCommandInteraction}.
	 * If a {@link SlashCommandInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link SlashCommandInteractionEvent} that was fired.
	 */
	private void handleSlashCommand(SlashCommandInteractionEvent event) {
		try {
			SlashCommandInteraction command = slashCommandIndex.get(event.getCommandPath());
			if (command == null) {
				throw new CommandNotRegisteredException(String.format("Slash Command \"%s\" is not registered.", event.getCommandPath()));
			} else {
				command.getHandler().handleSlashCommandInteraction(event);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles a single {@link UserContextInteraction}.
	 * If a {@link UserContextInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link UserContextInteractionEvent} that was fired.
	 */
	private void handleUserContextCommand(UserContextInteractionEvent event) {
		try {
			UserContextInteraction context = userContextIndex.get(event.getCommandPath());
			if (context == null) {
				throw new CommandNotRegisteredException(String.format("Context Command \"%s\" is not registered.", event.getCommandPath()));
			} else {
				context.getHandler().handleUserContextInteraction(event);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles a single {@link MessageContextInteraction}.
	 * If a {@link MessageContextInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link MessageContextInteractionEvent} that was fired.
	 */
	private void handleMessageContextCommand(MessageContextInteractionEvent event) {
		try {
			MessageContextInteraction context = messageContextIndex.get(event.getCommandPath());
			if (context == null) {
				throw new CommandNotRegisteredException(String.format("Context Command \"%s\" is not registered.", event.getCommandPath()));
			} else {
				context.getHandler().handleMessageContextInteraction(event);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Used to create one command name out of the SlashCommand, SlashSubCommandGroup and SlashSubCommand
	 *
	 * @return One combined string.
	 */
	@Contract(pure = true)
	private @NotNull String buildCommandPath(String... args) {
		return String.join("/", args);
	}

	/**
	 * Creates a new Instance of the given class.
	 *
	 * @param guild The slash command's guild. (if available)
	 * @param clazz The slash command's class.
	 * @return The Instance as a generic Object.
	 * @throws Exception If an error occurs.
	 */
	private @NotNull Object getClassInstance(Guild guild, Class<?> clazz) throws Exception {
		if (guild != null || !clazz.getSuperclass().equals(GlobalSlashCommand.class)) {
			try {
				return clazz.getConstructor(Guild.class).newInstance(guild);
			} catch (NoSuchMethodException ignored) {
			}
		}
		return clazz.getConstructor().newInstance();
	}

	/**
	 * Fired if Discord reports a {@link SlashCommandInteractionEvent}.
	 *
	 * @param event The {@link SlashCommandInteractionEvent} that was fired.
	 */
	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		CompletableFuture.runAsync(() -> this.handleSlashCommand(event));
	}


	/**
	 * Fired if Discord reports a {@link UserContextInteractionEvent}.
	 *
	 * @param event The {@link UserContextInteractionEvent} that was fired.
	 */
	@Override
	public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
		CompletableFuture.runAsync(() -> this.handleUserContextCommand(event));
	}

	/**
	 * Fired if Discord reports a {@link MessageContextInteractionEvent}.
	 *
	 * @param event The {@link MessageContextInteractionEvent} that was fired.
	 */
	@Override
	public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
		CompletableFuture.runAsync(() -> this.handleMessageContextCommand(event));
	}
}
