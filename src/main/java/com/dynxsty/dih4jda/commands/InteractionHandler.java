package com.dynxsty.dih4jda.commands;

import com.dynxsty.dih4jda.commands.interactions.context.IMessageContextCommand;
import com.dynxsty.dih4jda.commands.interactions.context.IUserContextCommand;
import com.dynxsty.dih4jda.commands.interactions.context.MessageContextInteraction;
import com.dynxsty.dih4jda.commands.interactions.context.UserContextInteraction;
import com.dynxsty.dih4jda.commands.interactions.context.dao.BaseContextCommand;
import com.dynxsty.dih4jda.commands.interactions.context.dao.GlobalContextCommand;
import com.dynxsty.dih4jda.commands.interactions.context.dao.GuildContextCommand;
import com.dynxsty.dih4jda.commands.interactions.slash.ISlashCommand;
import com.dynxsty.dih4jda.commands.interactions.slash.SlashCommandInteraction;
import com.dynxsty.dih4jda.commands.interactions.slash.dao.*;
import com.dynxsty.dih4jda.exceptions.InvalidParentException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.dynxsty.dih4jda.DIH4JDA.log;

public class InteractionHandler extends ListenerAdapter {

	private final String commandsPackage;
	private final Map<String, SlashCommandInteraction> slashCommandIndex;
	private final Map<String, MessageContextInteraction> messageContextIndex;
	private final Map<String, UserContextInteraction> userContextIndex;

	private final List<Class<? extends BaseSlashCommand>> guildCommands;
	private final List<Class<? extends BaseSlashCommand>> globalCommands;

	private final List<Class<? extends BaseContextCommand>> guildContexts;
	private final List<Class<? extends BaseContextCommand>> globalContexts;

	/**
	 * Constructs a new {@link InteractionHandler} from the supplied commands package.
	 *
	 * @param commandsPackage The package that houses the command classes.
	 */
	public InteractionHandler(String commandsPackage) {
		this.guildCommands = new ArrayList<>();
		this.globalCommands = new ArrayList<>();
		this.guildContexts = new ArrayList<>();
		this.globalContexts = new ArrayList<>();
		this.slashCommandIndex = new HashMap<>();
		this.messageContextIndex = new HashMap<>();
		this.userContextIndex = new HashMap<>();
		this.commandsPackage = commandsPackage;
	}

	/**
	 * Registers all slash commands. Loops through all classes found in the commands package that is a subclass of {@link BaseSlashCommand}.
	 * Goes through these steps with every iteration;
	 * <ol>
	 *     <li>Checks if the class is missing {@link CommandData} and doesn't register if it is.</li>
	 *     <li>Checks if the class is neither a subclass of {@link SlashSubcommand} nor {@link SlashSubcommandGroup} and registers it as regular command.</li>
	 *     <li>Checks if the class is a subclass of {@link SlashSubcommandGroup} if it is, the SlashCommandGroup is validated and another loop is fired following the two steps above for the group's sub commands.</li>
	 *     <li>Checks if the class is a subclass of {@link SlashSubcommand}, if it is, it is registered as a sub command.</li>
	 * </ol>
	 *
	 * @param jda The {@link JDA} instance.
	 * @throws Exception if anything goes wrong.
	 */
	public void registerSlashCommands(JDA jda) throws Exception {
		Reflections commands = new Reflections(this.commandsPackage);
		Set<Class<? extends BaseSlashCommand>> classes = commands.getSubTypesOf(BaseSlashCommand.class);
		for (Class<? extends BaseSlashCommand> c : classes) {
			if (c.getSuperclass().equals(GlobalSlashCommand.class)) {
				globalCommands.add(c);
			} else if (c.getSuperclass().equals(GuildSlashCommand.class)) {
				guildCommands.add(c);
			}
		}
		if (!this.guildCommands.isEmpty()) {
			for (Guild guild : jda.getGuilds()) {
				registerGuildCommand(guild);
			}
		}
		if (!this.globalCommands.isEmpty()) {
			registerGlobalCommand(jda);
		}
	}

	/**
	 * Registers a single Guild Command.
	 *
	 * @param guild The command's guild.
	 * @throws Exception If an error occurs.
	 */
	private void registerGuildCommand(@NotNull Guild guild) throws Exception {
		CommandListUpdateAction updateAction = guild.updateCommands();
		for (Class<? extends BaseSlashCommand> slashCommandClass : this.guildCommands) {
			BaseSlashCommand instance = (BaseSlashCommand) this.getClassInstance(guild, slashCommandClass);
			updateAction = registerCommand(updateAction, instance, slashCommandClass, guild);
		}
		log.info(String.format("[%s] Queuing Guild SlashCommands", guild.getName()));
		updateAction.queue();
	}

	/**
	 * Registers a single Global Command.
	 *
	 * @throws Exception If an error occurs.
	 */
	private void registerGlobalCommand(@NotNull JDA jda) throws Exception {
		CommandListUpdateAction updateAction = jda.updateCommands();
		for (Class<? extends BaseSlashCommand> slashCommandClass : this.globalCommands) {
			BaseSlashCommand instance = (BaseSlashCommand) this.getClassInstance(null, slashCommandClass);
			updateAction = this.registerCommand(updateAction, instance, slashCommandClass, null);
		}
		log.info("[*] Queuing Global SlashCommands");
		updateAction.queue();
	}

	/**
	 * Registers a single Command.
	 *
	 * @param action       The {@link CommandListUpdateAction}.
	 * @param command      The base command's instance.
	 * @param commandClass The base command's class.
	 * @param guild        The current guild (if available)
	 * @return The new {@link CommandListUpdateAction}.
	 * @throws Exception If an error occurs.
	 */
	private CommandListUpdateAction registerCommand(CommandListUpdateAction action, @NotNull BaseSlashCommand command, Class<? extends BaseSlashCommand> commandClass, @Nullable Guild guild) throws Exception {
		if (command.getCommandData() == null) {
			log.warn(String.format("Class %s is missing CommandData. It will be ignored.", commandClass.getName()));
			return null;
		}
		SlashCommandData commandData = command.getCommandData();
		if (command.getSubcommandGroupClasses() != null) {
			commandData = registerSubcommandGroup(command, command.getSubcommandGroupClasses(), guild);
		} else if (command.getSubcommandClasses() != null) {
			commandData = registerSubcommand(command, command.getSubcommandClasses(), guild);
		} else {
			slashCommandIndex.put(getFullCommandName(commandData.getName(), null, null),
					new SlashCommandInteraction((ISlashCommand) command, command.getCommandPrivileges()));
			log.info(String.format("\t[*] Registered command: /%s", command.getCommandData().getName()));
		}
		action.addCommands(commandData);
		return action;
	}

	/**
	 * Registers a single Command Group.
	 *
	 * @param command      The base command's instance.
	 * @param groupClasses All slash command group classes.
	 * @param guild        The current guild (if available)
	 * @return The new {@link CommandListUpdateAction}.
	 * @throws Exception If an error occurs.
	 */
	private SlashCommandData registerSubcommandGroup(@NotNull BaseSlashCommand command, Class<? extends SlashSubcommandGroup> @NotNull [] groupClasses, @Nullable Guild guild) throws Exception {
		SlashCommandData data = command.getCommandData();
		for (Class<? extends SlashSubcommandGroup> group : groupClasses) {
			SlashSubcommandGroup instance = (SlashSubcommandGroup) this.getClassInstance(guild, group);
			if (instance.getSubcommandGroupData() == null) {
				log.warn(String.format("Class %s is missing SubcommandGroupData. It will be ignored.", group.getName()));
				continue;
			}
			if (instance.getSubcommandClasses() == null) {
				log.warn(String.format("SubcommandGroup %s is missing Subcommands. It will be ignored.", instance.getSubcommandGroupData().getName()));
				continue;
			}
			SubcommandGroupData groupData = registerSubcommand(command, instance.getSubcommandGroupData(), instance.getSubcommandClasses(), guild);
			data.addSubcommandGroups(groupData);
		}
		return data;
	}

	/**
	 * Registers a single Sub Command for a Subcommand Group.
	 *
	 * @param command    The base command's instance.
	 * @param data       The subcommand group's data.
	 * @param subClasses All sub command classes.
	 * @param guild      The current guild (if available)
	 * @return The new {@link CommandListUpdateAction}.
	 * @throws Exception If an error occurs.
	 */
	private SubcommandGroupData registerSubcommand(BaseSlashCommand command, SubcommandGroupData data, Class<? extends SlashSubcommand> @NotNull [] subClasses, @Nullable Guild guild) throws Exception {
		for (Class<? extends SlashSubcommand> sub : subClasses) {
			SlashSubcommand instance = (SlashSubcommand) this.getClassInstance(guild, sub);
			if (instance.getSubcommandData() == null) {
				log.warn(String.format("Class %s is missing SubcommandData. It will be ignored.", sub.getName()));
				continue;
			}
			slashCommandIndex.put(getFullCommandName(command.getCommandData().getName(), data.getName(), instance.getSubcommandData().getName()),
					new SlashCommandInteraction((ISlashCommand) instance, command.getCommandPrivileges()));
			log.info(String.format("\t[*] Registered command: /%s", getFullCommandName(command.getCommandData().getName(), data.getName(), instance.getSubcommandData().getName())));
			data.addSubcommands(instance.getSubcommandData());
		}
		return data;
	}

	/**
	 * Registers a single Sub Command.
	 *
	 * @param command    The base command's instance.
	 * @param subClasses All sub command classes.
	 * @param guild      The current guild (if available)
	 * @return The new {@link CommandListUpdateAction}.
	 * @throws Exception If an error occurs.
	 */
	private SlashCommandData registerSubcommand(@NotNull BaseSlashCommand command, Class<? extends SlashSubcommand> @NotNull [] subClasses, @Nullable Guild guild) throws Exception {
		SlashCommandData data = command.getCommandData();
		for (Class<? extends SlashSubcommand> sub : subClasses) {
			SlashSubcommand instance = (SlashSubcommand) this.getClassInstance(guild, sub);
			if (instance.getSubcommandData() == null) {
				log.warn(String.format("Class %s is missing SubcommandData. It will be ignored.", sub.getName()));
				continue;
			}
			slashCommandIndex.put(getFullCommandName(data.getName(), data.getName(), instance.getSubcommandData().getName()),
					new SlashCommandInteraction((ISlashCommand) instance, command.getCommandPrivileges()));
			log.info(String.format("\t[*] Registered command: /%s %s", data.getName(), instance.getSubcommandData().getName()));
			data.addSubcommands(instance.getSubcommandData());
		}
		return data;
	}

	/**
	 * Registers all context commands. Loops through all classes found in the commands package that is a subclass of {@link BaseContextCommand}.
	 *
	 * @param jda The {@link JDA} instance.
	 * @throws Exception if anything goes wrong.
	 */
	public void registerContextCommands(JDA jda) throws Exception {
		Reflections commands = new Reflections(this.commandsPackage);
		Set<Class<? extends BaseContextCommand>> classes = commands.getSubTypesOf(BaseContextCommand.class);
		for (Class<? extends BaseContextCommand> c : classes) {
			if (c.getSuperclass().equals(GlobalContextCommand.class)) {
				globalContexts.add(c);
			} else if (c.getSuperclass().equals(GuildContextCommand.class)) {
				guildContexts.add(c);
			}
		}
		if (!this.guildContexts.isEmpty()) {
			for (Guild guild : jda.getGuilds()) {
				registerGuildContext(guild);
			}
		}
		if (!this.globalContexts.isEmpty()) {
			registerGlobalContext(jda);
		}
	}

	/**
	 * Registers a single Guild Context Command.
	 *
	 * @param guild The context command's guild.
	 * @throws Exception If an error occurs.
	 */
	private void registerGuildContext(@NotNull Guild guild) throws Exception {
		CommandListUpdateAction updateAction = guild.updateCommands();
		for (Class<? extends BaseContextCommand> contextCommandClass : this.guildContexts) {
			BaseContextCommand instance = (BaseContextCommand) this.getClassInstance(guild, contextCommandClass);
			updateAction = registerContext(updateAction, instance, contextCommandClass, guild);
		}
		log.info(String.format("[%s] Queuing Guild Context Commands", guild.getName()));
		updateAction.queue();
	}

	/**
	 * Registers a single Global Context Command.
	 *
	 * @throws Exception If an error occurs.
	 */
	private void registerGlobalContext(@NotNull JDA jda) throws Exception {
		CommandListUpdateAction updateAction = jda.updateCommands();
		for (Class<? extends BaseContextCommand> contextCommandClass : this.globalContexts) {
			BaseContextCommand instance = (BaseContextCommand) this.getClassInstance(null, contextCommandClass);
			updateAction = this.registerContext(updateAction, instance, contextCommandClass, null);
		}
		log.info("[Global] Queuing Global Context Commands");
		updateAction.queue();
	}

	/**
	 * Registers a single Context Command.
	 *
	 * @param action       The {@link CommandListUpdateAction}.
	 * @param command      The base context command's instance.
	 * @param commandClass The base context command's class.
	 * @param guild        The current guild (if available)
	 * @return The new {@link CommandListUpdateAction}.
	 * @throws Exception If an error occurs.
	 */
	private CommandListUpdateAction registerContext(CommandListUpdateAction action, @NotNull BaseContextCommand command, Class<? extends BaseContextCommand> commandClass, @Nullable Guild guild) throws Exception {
		if (command.getCommandData() == null) {
			log.warn(String.format("Class %s is missing CommandData. It will be ignored.", commandClass.getName()));
			return null;
		}
		CommandData commandData = command.getCommandData();
		if (commandData.getType() == Command.Type.MESSAGE) {
			messageContextIndex.put(commandData.getName(), new MessageContextInteraction((IMessageContextCommand) command));
		} else if (commandData.getType() == Command.Type.USER) {
			userContextIndex.put(commandData.getName(), new UserContextInteraction((IUserContextCommand) command));
		} else {
			log.error(String.format("Invalid Command Type \"%s\" for Context Command! This command will be ignored.", commandData.getType()));
			return action;
		}
		log.info(String.format("\t[*] Registered context command: %s", command.getCommandData().getName()));
		action.addCommands(commandData);
		return action;
	}

	/**
	 * Handles a single {@link SlashCommandInteraction}.
	 * If a {@link SlashCommandInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link SlashCommandInteractionEvent} that was fired.
	 */
	private void handleSlashCommand(SlashCommandInteractionEvent event) {
		try {
			SlashCommandInteraction command = slashCommandIndex.get(getFullCommandName(event.getName(), event.getSubcommandGroup(), event.getSubcommandName()));
			command.getHandler().handleSlashCommandInteraction(event);
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
			UserContextInteraction context = userContextIndex.get(event.getName());
			context.getHandler().handleUserContextInteraction(event);
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
			MessageContextInteraction context = messageContextIndex.get(event.getName());
			context.getHandler().handleMessageContextInteraction(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Used to create one command name out of the SlashCommand, SlashSubCommandGroup and SlashSubCommand
	 *
	 * @param first  The SlashCommand's name.
	 * @param second The SlashSubCommandGroup's name.
	 * @param third  The SlashSubCommand's name.
	 * @return One combined string.
	 */
	@Contract(pure = true)
	private @NotNull String getFullCommandName(String first, String second, String third) {
		return String.format("%s %s %s", first, second, third);
	}

	/**
	 * Creates a new Instance of the given class.
	 *
	 * @param guild The slash command's guild. (if available)
	 * @param clazz The slash command's class.
	 * @return The Instance as a generic Object.
	 * @throws Exception If an error occurs.
	 */
	private Object getClassInstance(Guild guild, Class<?> clazz) throws Exception {
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
