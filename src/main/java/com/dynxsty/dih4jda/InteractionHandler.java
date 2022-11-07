package com.dynxsty.dih4jda;

import com.dynxsty.dih4jda.config.DIH4JDAConfig;
import com.dynxsty.dih4jda.events.DIH4JDAEvent;
import com.dynxsty.dih4jda.exceptions.CommandNotRegisteredException;
import com.dynxsty.dih4jda.exceptions.DIH4JDAException;
import com.dynxsty.dih4jda.interactions.ComponentIdBuilder;
import com.dynxsty.dih4jda.interactions.commands.AutoCompletable;
import com.dynxsty.dih4jda.interactions.commands.ContextCommand;
import com.dynxsty.dih4jda.interactions.commands.ExecutableCommand;
import com.dynxsty.dih4jda.interactions.commands.RegistrationType;
import com.dynxsty.dih4jda.interactions.commands.SlashCommand;
import com.dynxsty.dih4jda.interactions.components.ButtonHandler;
import com.dynxsty.dih4jda.interactions.components.ModalHandler;
import com.dynxsty.dih4jda.interactions.components.SelectMenuHandler;
import com.dynxsty.dih4jda.util.AutoCompleteUtils;
import com.dynxsty.dih4jda.util.Checks;
import com.dynxsty.dih4jda.util.ClassUtils;
import com.dynxsty.dih4jda.util.ClassWalker;
import com.dynxsty.dih4jda.util.CommandUtils;
import com.dynxsty.dih4jda.util.Pair;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * The Handler class, that finds, registers and handles all Commands and other Interactions.
 *
 * @see DIH4JDABuilder#disableAutomaticCommandRegistration()
 * @see DIH4JDA#registerInteractions()
 */
public class InteractionHandler extends ListenerAdapter {

	/**
	 * The main {@link DIH4JDA} instance.
	 */
	private final DIH4JDA dih4jda;

	/**
	 * The instance's configuration;
	 */
	private final DIH4JDAConfig config;

	/**
	 * An Index of all {@link SlashCommand}s.
	 *
	 * @see InteractionHandler#findSlashCommands(String)
	 */
	private final Map<String, SlashCommand> slashCommandIndex;

	/**
	 * An Index of all {@link SlashCommand.Subcommand}s.
	 *
	 * @see InteractionHandler#findSlashCommands(String)
	 */
	private final Map<String, SlashCommand.Subcommand> subcommandIndex;

	/**
	 * An Index of all {@link ContextCommand.Message}s.
	 *
	 * @see InteractionHandler#findContextCommands(String)
	 */
	private final Map<String, ContextCommand.Message> messageContextIndex;

	/**
	 * An Index of all {@link ContextCommand.User}s.
	 *
	 * @see InteractionHandler#findContextCommands(String)
	 */
	private final Map<String, ContextCommand.User> userContextIndex;

	/**
	 * An Index of all {@link AutoCompletable}s.
	 *
	 * @see InteractionHandler#findSlashCommands(String)
	 */
	private final Map<String, AutoCompletable> autoCompleteIndex;

	protected final Set<SlashCommand> commands;
	protected final Set<ContextCommand> contexts;

	/**
	 * Constructs a new {@link InteractionHandler} from the supplied commands package.
	 *
	 * @param dih4jda The {@link DIH4JDA} instance.
	 */
	protected InteractionHandler(@Nonnull DIH4JDA dih4jda) {
		this.dih4jda = dih4jda;
		config = dih4jda.getConfig();

		commands = new HashSet<>();
		contexts = new HashSet<>();
		for (String pkg : config.getCommandPackages()) {
			try {
				findSlashCommands(pkg);
				findContextCommands(pkg);
			} catch (ReflectiveOperationException | DIH4JDAException e) {
				DIH4JDALogger.error("An error occurred while initializing commands in package %s: %s", pkg, e.getMessage());
			}
		}
		// initialize indexes
		slashCommandIndex = new HashMap<>();
		subcommandIndex = new HashMap<>();
		messageContextIndex = new HashMap<>();
		userContextIndex = new HashMap<>();
		autoCompleteIndex = new HashMap<>();
	}

	/**
	 * Registers all interactions.
	 * This method can be accessed from the {@link DIH4JDA} instance.
	 * <br>This is automatically executed each time the {@link ListenerAdapter#onReady(ReadyEvent)} event is executed.
	 * (can be disabled using {@link DIH4JDABuilder#disableAutomaticCommandRegistration()})
	 *
	 * @throws ReflectiveOperationException If an error occurs.
	 */
	public void registerInteractions() throws ReflectiveOperationException {
		// register commands for each guild
		Pair<Set<SlashCommand>, Set<ContextCommand>> data = new Pair<>(getSlashCommands(), getContextCommandData());
		for (Guild guild : config.getJDA().getGuilds()) {
			Pair<Set<SlashCommand>, Set<ContextCommand>> guildData = CommandUtils.filterByType(data, RegistrationType.GUILD);
			// check if smart queuing is enabled
			if (config.isGuildSmartQueue()) {
				guildData = new SmartQueue(guildData.getFirst(), guildData.getSecond(), config.isDeleteUnknownCommands()).checkGuild(guild);
			}
			// upsert all guild commands
			if (!guildData.getFirst().isEmpty() || !guildData.getSecond().isEmpty()) {
				upsert(guild, guildData.getFirst(), guildData.getSecond());
			}
		}
		Pair<Set<SlashCommand>, Set<ContextCommand>> globalData = CommandUtils.filterByType(data, RegistrationType.GLOBAL);
		// check if smart queuing is enabled
		if (config.isGlobalSmartQueue()) {
			globalData = new SmartQueue(globalData.getFirst(), globalData.getSecond(), config.isDeleteUnknownCommands()).checkGlobal(config.getJDA());
		}
		// upsert all global commands
		if (!globalData.getFirst().isEmpty() || !globalData.getSecond().isEmpty()) {
			upsert(config.getJDA(), globalData.getFirst(), globalData.getSecond());
			DIH4JDALogger.info(DIH4JDALogger.Type.COMMANDS_QUEUED, "Queued %s global command(s): %s",
					globalData.getFirst().size() + globalData.getSecond().size(), CommandUtils.getNames(globalData.getSecond(), globalData.getFirst()));
		}
		if (!autoCompleteIndex.isEmpty()) {
			// print autocomplete bindings
			DIH4JDALogger.info("Created %s AutoComplete binding(s): %s", autoCompleteIndex.size(),
					autoCompleteIndex.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue().getClass().getSimpleName()).collect(Collectors.joining(", ")));
		}
	}

	/**
	 * Creates global commands from the given (Slash-) CommandData
	 *
	 * @param jda         The {@link JDA} instance.
	 * @param slashCommand   A set of {@link SlashCommandData}.
	 * @param contextCommands A set of {@link CommandData},
	 */
	private void upsert(@Nonnull JDA jda, @Nonnull Set<SlashCommand> slashCommand, @Nonnull Set<ContextCommand> contextCommands) {
		slashCommand.forEach(data -> jda.upsertCommand(data.getSlashCommandData()).queue());
		contextCommands.forEach(data -> jda.upsertCommand(data.getCommandData()).queue());
	}

	/**
	 * Creates guild commands from the given (Slash-) CommandData
	 *
	 * @param guild       The {@link Guild}.
	 * @param slashCommands   A set of {@link SlashCommandData}.
	 * @param contextCommands A set of {@link CommandData},
	 */
	private void upsert(@Nonnull Guild guild, @Nonnull Set<SlashCommand> slashCommands, @Nonnull Set<ContextCommand> contextCommands) {
		StringBuilder commandNames = new StringBuilder();
		slashCommands.forEach(data -> {
			Pair<Boolean, Long[]> pair = data.getRequiredGuilds();
			if (pair.getFirst() == null) {
				guild.upsertCommand(data.getSlashCommandData()).queue();
				commandNames.append(", /").append(data.getSlashCommandData().getName());
			} else {
				if (pair.getFirst()) {
					if (Arrays.asList(pair.getSecond()).contains(guild.getIdLong())) {
						guild.upsertCommand(data.getSlashCommandData()).queue();
						commandNames.append(", /").append(data.getSlashCommandData().getName());
					} else {
						DIH4JDALogger.info(DIH4JDALogger.Type.SLASH_COMMAND_SKIPPED, "Skipping Registration of /%s for Guild: %s", data.getSlashCommandData().getName(), guild.getName());
					}
				}
			}
		});
		contextCommands.forEach(data -> {
			Pair<Boolean, Long[]> pair = data.getRequiredGuilds();
			if (pair.getFirst()) {
				if (Arrays.asList(pair.getSecond()).contains(guild.getIdLong())) {
					guild.upsertCommand(data.getCommandData()).queue();
					commandNames.append(", ").append(data.getCommandData().getName());
				} else {
					DIH4JDALogger.info(DIH4JDALogger.Type.SLASH_COMMAND_SKIPPED, "Skipping Registration of %s for Guild: %s", data.getCommandData().getName(), guild.getName());
				}
			}
		});
		if (!commandNames.toString().isEmpty()) {
			DIH4JDALogger.info(DIH4JDALogger.Type.COMMANDS_QUEUED, "Queued %s command(s) in guild %s: %s",
					slashCommands.size() + contextCommands.size(), guild.getName(), commandNames.substring(2));
		}
	}

	/**
	 * Finds all Slash Commands using the {@link ClassWalker}.
	 * Loops through all classes found in the commands package that is a subclass of
	 * {@link SlashCommand}.
	 */
	private void findSlashCommands(String pkg) throws ReflectiveOperationException, DIH4JDAException {
		ClassWalker classes = new ClassWalker(pkg);
		Set<Class<? extends SlashCommand>> subTypes = classes.getSubTypesOf(SlashCommand.class);
		for (Class<? extends SlashCommand> subType : subTypes) {
			if (Checks.checkEmptyConstructor(subType)) {
				commands.add((SlashCommand) ClassUtils.getInstance(subType));
			} else {
				DIH4JDALogger.error("Could not initialize %s! The class MUST contain a empty public constructor.", subType.getName());
			}
		}
	}

	/**
	 * Finds all Context Commands using the {@link ClassWalker}.
	 * Loops through all classes found in the commands package that is a subclass of
	 * {@link ContextCommand}.
	 */
	private void findContextCommands(String pkg) throws ReflectiveOperationException, DIH4JDAException {
		ClassWalker classes = new ClassWalker(pkg);
		Set<Class<? extends ContextCommand>> subTypes = classes.getSubTypesOf(ContextCommand.class);
		for (Class<? extends ContextCommand> subType : subTypes) {
			if (Checks.checkEmptyConstructor(subType)) {
				contexts.add((ContextCommand) ClassUtils.getInstance(subType));
			} else {
				DIH4JDALogger.error("Could not initialize %s! The class MUST contain a empty public constructor.", subType.getName());
			}
		}
	}

	/**
	 * Gets all Commands that were found in {@link InteractionHandler#findSlashCommands(String)} and adds
	 * them to the {@link InteractionHandler#slashCommandIndex}.
	 */
	private @Nonnull Set<SlashCommand> getSlashCommands() {
		Set<SlashCommand> commands = new HashSet<>();
		for (SlashCommand command : this.commands) {
			if (command != null) {
				SlashCommandData data = getBaseCommandData(command, command.getClass());
				if (data != null) {
					command.setSlashCommandData(data);
				}
				if (command.getRegistrationType() != RegistrationType.GUILD && command.getRequiredGuilds().getFirst()) {
					throw new UnsupportedOperationException(command.getClass().getName() + " attempted to require guilds for a non-global command!");
				}
				searchForAutoCompletable(command, command.getClass());
				commands.add(command);
			}
		}
		return commands;
	}

	/**
	 * Searches for Base- or Subcommand which implement the {@link AutoCompletable} interface.
	 *
	 * @param command The base {@link SlashCommand}.
	 * @param clazz   The command's class.
	 */
	private void searchForAutoCompletable(@Nonnull SlashCommand command, @Nonnull Class<? extends SlashCommand> clazz) {
		// check base command
		String baseName = command.getSlashCommandData().getName();
		if (Checks.checkImplementation(clazz, AutoCompletable.class)) {
			autoCompleteIndex.put(baseName, (AutoCompletable) command);
		}
		// check subcommands
		for (SlashCommand.Subcommand child : command.getSubcommands()) {
			if (Checks.checkImplementation(child.getClass(), AutoCompletable.class)) {
				autoCompleteIndex.put(CommandUtils.buildCommandPath(baseName, child.getSubcommandData().getName()), (AutoCompletable) child);
			}
		}
		// check subcommand groups
		for (Map.Entry<SubcommandGroupData, SlashCommand.Subcommand[]> childGroup : command.getSubcommandGroups().entrySet()) {
			String groupName = childGroup.getKey().getName();
			// check subcommands
			for (SlashCommand.Subcommand child : childGroup.getValue()) {
				if (Checks.checkImplementation(child.getClass(), AutoCompletable.class)) {
					autoCompleteIndex.put(CommandUtils.buildCommandPath(baseName, groupName, child.getSubcommandData().getName()), (AutoCompletable) child);
				}
			}
		}
	}

	/**
	 * Gets the complete {@link SlashCommandData} (including Subcommands & Subcommand Groups) of a single {@link SlashCommand}.
	 *
	 * @param command      The base command's instance.
	 * @param commandClass The base command's class.
	 * @return The new {@link CommandListUpdateAction}.
	 */
	private @Nullable SlashCommandData getBaseCommandData(@Nonnull SlashCommand command, @Nonnull Class<? extends SlashCommand> commandClass) {
		// find component (and modal) handlers
		if (command.getSlashCommandData() == null) {
			DIH4JDALogger.warn("Class %s is missing CommandData. It will be ignored.", commandClass.getName());
			return null;
		}
		SlashCommandData commandData = command.getSlashCommandData();
		if (command.getSubcommandGroups() != null && !command.getSubcommandGroups().isEmpty()) {
			commandData.addSubcommandGroups(getSubcommandGroupData(command));
		}
		if (command.getSubcommands() != null && command.getSubcommands().length != 0) {
			commandData.addSubcommands(getSubcommandData(command, command.getSubcommands(), null));
		}
		if (command.getSubcommandGroups() != null && command.getSubcommandGroups().isEmpty()
				&& command.getSubcommands() != null && command.getSubcommands().length == 0) {
			slashCommandIndex.put(CommandUtils.buildCommandPath(commandData.getName()), command);
			DIH4JDALogger.info(DIH4JDALogger.Type.SLASH_COMMAND_REGISTERED, "\t[*] Registered command: /%s (%s)", command.getSlashCommandData().getName(), command.getRegistrationType().name());
		}
		return commandData;
	}

	/**
	 * Gets all {@link SubcommandGroupData} (including Subcommands) of a single {@link SlashCommand}.
	 *
	 * @param command The base command's instance.
	 * @return All {@link SubcommandGroupData} stored in a List.
	 */
	private @Nonnull Set<SubcommandGroupData> getSubcommandGroupData(@Nonnull SlashCommand command) {
		Set<SubcommandGroupData> groupDataList = new HashSet<>();
		for (Map.Entry<SubcommandGroupData, SlashCommand.Subcommand[]> group : command.getSubcommandGroups().entrySet()) {
			if (group != null) {
				if (group.getKey() == null) {
					DIH4JDALogger.warn("Class %s is missing SubcommandGroupData. It will be ignored.", group.getClass().getSimpleName());
					continue;
				}
				if (group.getValue() == null || group.getValue().length == 0) {
					DIH4JDALogger.warn("SubcommandGroup %s is missing Subcommands. It will be ignored.", group.getKey().getName());
					continue;
				}
				SubcommandGroupData groupData = group.getKey();
				groupData.addSubcommands(getSubcommandData(command, group.getValue(), groupData.getName()));
				groupDataList.add(groupData);
			}
		}
		return groupDataList;
	}

	/**
	 * Gets all {@link SubcommandData} from the given array of {@link SlashCommand.Subcommand} classes.
	 *
	 * @param command      The base command's instance.
	 * @param subcommands  All sub command classes.
	 * @param subGroupName The Subcommand Group's name. (if available)
	 * @return The new {@link CommandListUpdateAction}.
	 */
	private @Nonnull Set<SubcommandData> getSubcommandData(@Nonnull SlashCommand command, @Nonnull SlashCommand.Subcommand[] subcommands, @Nullable String subGroupName) {
		Set<SubcommandData> subDataList = new HashSet<>();
		for (SlashCommand.Subcommand subcommand : subcommands) {
			if (subcommand != null) {
				if (subcommand.getSubcommandData() == null) {
					DIH4JDALogger.warn("Class %s is missing SubcommandData. It will be ignored.", subcommand.getClass().getSimpleName());
					continue;
				}
				String commandPath;
				if (subGroupName == null) {
					commandPath = CommandUtils.buildCommandPath(command.getSlashCommandData().getName(), subcommand.getSubcommandData().getName());
				} else {
					commandPath = CommandUtils.buildCommandPath(command.getSlashCommandData().getName(), subGroupName, subcommand.getSubcommandData().getName());
				}
				subcommandIndex.put(commandPath, subcommand);
				DIH4JDALogger.info(DIH4JDALogger.Type.SLASH_COMMAND_REGISTERED, "\t[*] Registered command: /%s (%s)", commandPath, command.getRegistrationType().name());
				subDataList.add(subcommand.getSubcommandData());
			}
		}
		return subDataList;
	}

	/**
	 * Gets all Guild Context commands registered in {@link InteractionHandler#findContextCommands(String)} and
	 * returns their {@link CommandData} as a List.
	 *
	 */
	private @Nonnull Set<ContextCommand> getContextCommandData() {
		Set<ContextCommand> commands = new HashSet<>();
		for (ContextCommand context : contexts) {
			if (context != null) {
				CommandData data = getContextCommandData(context, context.getClass());
				if (data != null) {
					context.setCommandData(data);
				}
				if (context.getRegistrationType() != RegistrationType.GUILD && context.getRequiredGuilds().getFirst()) {
					throw new UnsupportedOperationException(context.getClass().getName() + " attempted to require guilds for a non-global command!");
				}
				commands.add(context);
			}
		}
		return commands;
	}

	/**
	 * Gets the complete {@link CommandData} from a single {@link ContextCommand}.
	 *
	 * @param command      The base context command's instance.
	 * @param commandClass The base context command's class.
	 * @return The new {@link CommandListUpdateAction}.
	 */
	private @Nullable CommandData getContextCommandData(@Nonnull ContextCommand command, @Nonnull Class<? extends ContextCommand> commandClass) {
		if (command.getCommandData() == null) {
			DIH4JDALogger.warn("Class %s is missing CommandData. It will be ignored.", commandClass.getName());
			return null;
		}
		CommandData commandData = command.getCommandData();
		if (commandData.getType() == Command.Type.MESSAGE) {
			messageContextIndex.put(commandData.getName(), (ContextCommand.Message) command);
		} else if (commandData.getType() == Command.Type.USER) {
			userContextIndex.put(commandData.getName(), (ContextCommand.User) command);
		} else {
			DIH4JDALogger.error("Invalid Command Type \"%s\" for Context Command! This command will be ignored.", commandData.getType());
			return null;
		}
		DIH4JDALogger.info(DIH4JDALogger.Type.CONTEXT_COMMAND_REGISTERED, "\t[*] Registered context command: %s (%s)", command.getCommandData().getName(), command.getRegistrationType().name());
		return commandData;
	}

	/**
	 * Handles a single {@link SlashCommand} or {@link SlashCommand.Subcommand}.
	 * If a {@link SlashCommandInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link SlashCommandInteractionEvent} that was fired.
	 */
	private void handleSlashCommand(@Nonnull SlashCommandInteractionEvent event) throws CommandNotRegisteredException {
		String path = event.getCommandPath();
		ExecutableCommand<SlashCommandInteractionEvent> executable = slashCommandIndex.containsKey(path) ?
				slashCommandIndex.get(path) : subcommandIndex.get(path);
		if (executable == null) {
			if (config.isThrowUnregisteredException()) {
				throw new CommandNotRegisteredException(String.format("Slash Command \"%s\" is not registered.", path));
			}
		} else {
			if (passesRequirements(event, executable.getSlashCommand().getRequiredPermissions(),
					executable.getSlashCommand().getRequiredUsers(), executable.getSlashCommand().getRequiredRoles())) {
				if (slashCommandIndex.containsKey(event.getCommandPath())) {
					slashCommandIndex.get(path).execute(event);
				} else {
					subcommandIndex.get(path).execute(event);
				}
			}
			// removes all cached choices, if there are any.
			AutoCompleteUtils.removeFromCache(
					event.getCommandPath(),
					event.getUser().getId(),
					event.getGuild() == null ? "0" : event.getGuild().getId()
			);
		}
	}

	/**
	 * Handles a single {@link ContextCommand.User}.
	 * If a {@link UserContextInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link UserContextInteractionEvent} that was fired.
	 */
	private void handleUserContextCommand(@Nonnull UserContextInteractionEvent event) throws CommandNotRegisteredException {
		ContextCommand.User context = userContextIndex.get(event.getCommandPath());
		if (context == null) {
			if (config.isThrowUnregisteredException()) {
				throw new CommandNotRegisteredException(String.format("Context Command \"%s\" is not registered.", event.getCommandPath()));
			}
		} else {
			if (passesRequirements(event, context.getRequiredPermissions(), context.getRequiredUsers(), context.getRequiredRoles())) {
				context.execute(event);
			}
		}
	}

	/**
	 * Handles a single {@link ContextCommand.Message}.
	 * If a {@link MessageContextInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link MessageContextInteractionEvent} that was fired.
	 */
	private void handleMessageContextCommand(@Nonnull MessageContextInteractionEvent event) throws CommandNotRegisteredException {
		ContextCommand.Message context = messageContextIndex.get(event.getCommandPath());
		if (context == null) {
			if (config.isThrowUnregisteredException()) {
				throw new CommandNotRegisteredException(String.format("Context Command \"%s\" is not registered.", event.getCommandPath()));
			}
		} else {
			if (passesRequirements(event, context.getRequiredPermissions(), context.getRequiredUsers(), context.getRequiredRoles())) {
				context.execute(event);
			}
		}
	}

	/**
	 * Checks if the given {@link CommandInteraction} passes the set {@link Command}s requirements.
	 * If not, this will then fire the corresponding event using {@link DIH4JDAEvent#fire(Set, Object...)}
	 *
	 * @param interaction The {@link CommandInteraction}.
	 * @param permissions A set of required {@link Permission}s.
	 * @param userIds     A set of required users ids.
	 * @param roleIds     A set of required role ids.
	 * @return Whether the event was fired.
	 * @since v1.5
	 */
	private boolean passesRequirements(@Nonnull CommandInteraction interaction, Permission[] permissions, Long[] userIds, Long[] roleIds) {
		if (permissions != null && permissions.length != 0 && interaction.isFromGuild() && interaction.getMember() != null && !interaction.getMember().hasPermission(permissions)) {
			DIH4JDAEvent.INSUFFICIENT_PERMISSIONS.fire(dih4jda.getListeners(), interaction, permissions);
			return false;
		}
		if (userIds != null && userIds.length != 0 && !Arrays.asList(userIds).contains(interaction.getUser().getIdLong())) {
			DIH4JDAEvent.INVALID_USER.fire(dih4jda.getListeners(), interaction, userIds);
			return false;
		}
		if (interaction.isFromGuild() && interaction.getGuild() != null && interaction.getMember() != null) {
			Member member = interaction.getMember();
			if (roleIds != null && roleIds.length != 0 && !member.getRoles().isEmpty() && member.getRoles().stream().noneMatch(r -> Arrays.asList(roleIds).contains(r.getIdLong()))) {
				DIH4JDAEvent.INVALID_ROLE.fire(dih4jda.getListeners(), interaction, roleIds);
				return false;
			}
		}
		return true;
	}

	/**
	 * Fired if Discord reports a {@link SlashCommandInteractionEvent}.
	 *
	 * @param event The {@link SlashCommandInteractionEvent} that was fired.
	 */
	@Override
	public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
		CompletableFuture.runAsync(() -> {
			try {
				handleSlashCommand(event);
			} catch (Exception e) {
				DIH4JDAEvent.COMMAND_EXCEPTION.fire(dih4jda.getListeners(), event, e);
			}
		}, config.getExecutor());
	}

	/**
	 * Fired if Discord reports a {@link UserContextInteractionEvent}.
	 *
	 * @param event The {@link UserContextInteractionEvent} that was fired.
	 */
	@Override
	public void onUserContextInteraction(@Nonnull UserContextInteractionEvent event) {
		CompletableFuture.runAsync(() -> {
			try {
				handleUserContextCommand(event);
			} catch (Exception e) {
				DIH4JDAEvent.COMMAND_EXCEPTION.fire(dih4jda.getListeners(), event, e);
			}
		}, config.getExecutor());
	}

	/**
	 * Fired if Discord reports a {@link MessageContextInteractionEvent}.
	 *
	 * @param event The {@link MessageContextInteractionEvent} that was fired.
	 */
	@Override
	public void onMessageContextInteraction(@Nonnull MessageContextInteractionEvent event) {
		CompletableFuture.runAsync(() -> {
			try {
				handleMessageContextCommand(event);
			} catch (Exception e) {
				DIH4JDAEvent.COMMAND_EXCEPTION.fire(dih4jda.getListeners(), event, e);
			}
		}, config.getExecutor());
	}

	/**
	 * Fired if Discord reports a {@link CommandAutoCompleteInteractionEvent}.
	 *
	 * @param event The {@link CommandAutoCompleteInteractionEvent} that was fired.
	 */
	@Override
	public void onCommandAutoCompleteInteraction(@Nonnull CommandAutoCompleteInteractionEvent event) {
		CompletableFuture.runAsync(() -> {
			try {
				AutoCompletable autoComplete = autoCompleteIndex.get(event.getCommandPath());
				if (autoComplete != null) {
					autoComplete.handleAutoComplete(event, event.getFocusedOption());
				}
			} catch (Exception e) {
				DIH4JDAEvent.AUTOCOMPLETE_EXCEPTION.fire(dih4jda.getListeners(), event, e);
			}
		}, config.getExecutor());
	}

	/**
	 * Fired if Discord reports a {@link ButtonInteractionEvent}.
	 *
	 * @param event The {@link ButtonInteractionEvent} that was fired.
	 */
	@Override
	public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
		CompletableFuture.runAsync(() -> {
			try {
				Optional<ButtonHandler> buttonOptional = dih4jda.getButtonHandlers().entrySet().stream()
						.filter(f -> f.getKey().contains(ComponentIdBuilder.split(event.getComponentId())[0]))
						.map(Map.Entry::getValue)
						.findFirst();
				if (buttonOptional.isEmpty()) {
					DIH4JDALogger.warn(DIH4JDALogger.Type.BUTTON_NOT_FOUND, "Button with id \"%s\" could not be found.", event.getComponentId());
				} else {
					buttonOptional.get().handleButton(event, event.getButton());
				}
			} catch (Exception e) {
				DIH4JDAEvent.COMPONENT_EXCEPTION.fire(dih4jda.getListeners(), event, e);
			}
		}, config.getExecutor());
	}

	/**
	 * Fired if Discord reports a {@link SelectMenuInteractionEvent}.
	 *
	 * @param event The {@link SelectMenuInteractionEvent} that was fired.
	 */
	@Override
	public void onSelectMenuInteraction(@Nonnull SelectMenuInteractionEvent event) {
		CompletableFuture.runAsync(() -> {
			try {
				Optional<SelectMenuHandler> selectMenuOptional = dih4jda.getSelectMenuHandlers().entrySet().stream()
						.filter(f -> f.getKey().contains(ComponentIdBuilder.split(event.getComponentId())[0]))
						.map(Map.Entry::getValue)
						.findFirst();
				if (selectMenuOptional.isEmpty()) {
					DIH4JDALogger.warn(DIH4JDALogger.Type.SELECT_MENU_NOT_FOUND, "Select Menu with id \"%s\" could not be found.", event.getComponentId());
				} else {
					selectMenuOptional.get().handleSelectMenu(event, event.getValues());
				}
			} catch (Exception e) {
				DIH4JDAEvent.COMPONENT_EXCEPTION.fire(dih4jda.getListeners(), event, e);
			}
		}, config.getExecutor());
	}

	/**
	 * Fired if Discord reports a {@link ModalInteractionEvent}.
	 *
	 * @param event The {@link ModalInteractionEvent} that was fired.
	 */
	@Override
	public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
		CompletableFuture.runAsync(() -> {
			try {
				Optional<ModalHandler> modalOptional = dih4jda.getModalHandlers().entrySet().stream()
						.filter(f -> f.getKey().contains(ComponentIdBuilder.split(event.getModalId())[0]))
						.map(Map.Entry::getValue)
						.findFirst();
				if (modalOptional.isEmpty()) {
					DIH4JDALogger.warn(DIH4JDALogger.Type.MODAL_NOT_FOUND, "Modal with id \"%s\" could not be found.", event.getModalId());
				} else {
					modalOptional.get().handleModal(event, event.getValues());
				}
			} catch (Exception e) {
				DIH4JDAEvent.MODAL_EXCEPTION.fire(dih4jda.getListeners(), event, e);
			}
		}, config.getExecutor());
	}
}
