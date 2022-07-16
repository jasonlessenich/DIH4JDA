package com.dynxsty.dih4jda;

import com.dynxsty.dih4jda.config.DIH4JDAConfig;
import com.dynxsty.dih4jda.events.DIH4JDAListenerAdapter;
import com.dynxsty.dih4jda.exceptions.CommandNotRegisteredException;
import com.dynxsty.dih4jda.interactions.ComponentIdBuilder;
import com.dynxsty.dih4jda.interactions.commands.*;
import com.dynxsty.dih4jda.interactions.commands.model.UnqueuedCommandData;
import com.dynxsty.dih4jda.interactions.commands.model.UnqueuedSlashCommandData;
import com.dynxsty.dih4jda.interactions.components.ButtonHandler;
import com.dynxsty.dih4jda.interactions.components.ModalHandler;
import com.dynxsty.dih4jda.interactions.components.SelectMenuHandler;
import com.dynxsty.dih4jda.util.Checks;
import com.dynxsty.dih4jda.util.ClassUtils;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.util.*;
import java.util.concurrent.CompletableFuture;

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
	 * @see InteractionHandler#findSlashCommands()
	 */
	private final Map<String, SlashCommand> slashCommandIndex;

	/**
	 * An Index of all {@link SlashCommand.Subcommand}s.
	 *
	 * @see InteractionHandler#findSlashCommands()
	 */
	private final Map<String, SlashCommand.Subcommand> subcommandIndex;

	/**
	 * An Index of all {@link ContextCommand.Message}s.
	 *
	 * @see InteractionHandler#findContextCommands()
	 */
	private final Map<String, ContextCommand.Message> messageContextIndex;

	/**
	 * An Index of all {@link ContextCommand.User}s.
	 *
	 * @see InteractionHandler#findContextCommands()
	 */
	private final Map<String, ContextCommand.User> userContextIndex;

	/**
	 * An Index of all {@link AutoCompletable}s.
	 *
	 * @see InteractionHandler#findSlashCommands()
	 */
	private final Map<String, AutoCompletable> autoCompleteIndex;

	private final Set<Class<? extends SlashCommand>> commands;
	private final Set<Class<? extends ContextCommand>> contexts;

	/**
	 * Constructs a new {@link InteractionHandler} from the supplied commands package.
	 *
	 * @param dih4jda The {@link DIH4JDA} instance.
	 */
	protected InteractionHandler(@NotNull DIH4JDA dih4jda) {
		this.dih4jda = dih4jda;
		config = dih4jda.getConfig();

		commands = findSlashCommands();
		contexts = findContextCommands();
		// remove own implementations
		contexts.removeAll(List.of(
				ContextCommand.User.class,
				ContextCommand.Message.class));

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
		Pair<Set<UnqueuedSlashCommandData>, Set<UnqueuedCommandData>> data = new Pair<>(getSlashCommandData(), getContextCommandData());
		for (Guild guild : config.getJDA().getGuilds()) {
			Pair<Set<UnqueuedSlashCommandData>, Set<UnqueuedCommandData>> guildData = CommandUtils.filterByType(data, RegistrationType.GUILD);
			// check if smart queuing is enabled
			if (config.isGuildSmartQueue()) {
				guildData = new SmartQueue(guildData.getFirst(), guildData.getSecond(), config.isDeleteUnknownCommands()).checkGuild(guild);
			}
			// upsert all guild commands
			if (!guildData.getFirst().isEmpty() || !guildData.getSecond().isEmpty()) {
				upsert(guild, guildData.getFirst(), guildData.getSecond());
			}
		}
		Pair<Set<UnqueuedSlashCommandData>, Set<UnqueuedCommandData>> globalData = CommandUtils.filterByType(data, RegistrationType.GLOBAL);
		// check if smart queuing is enabled
		if (config.isGlobalSmartQueue()) {
			globalData = new SmartQueue(globalData.getFirst(), globalData.getSecond(), config.isDeleteUnknownCommands()).checkGlobal(config.getJDA());
		}
		// upsert all global commands
		if (!globalData.getFirst().isEmpty() || !globalData.getSecond().isEmpty()) {
			upsert(config.getJDA(), globalData.getFirst(), globalData.getSecond());
			DIH4JDALogger.info(String.format("Queued %s global command(s): %s", globalData.getFirst().size() + globalData.getSecond().size(),
					CommandUtils.getNames(globalData.getSecond(), globalData.getFirst())), DIH4JDALogger.Type.COMMANDS_QUEUED);
		}
	}

	/**
	 * Creates global commands from the given (Slash-) CommandData
	 *
	 * @param jda         The {@link JDA} instance.
	 * @param slashData   A set of {@link SlashCommandData}.
	 * @param commandData A set of {@link CommandData},
	 */
	private void upsert(JDA jda, @NotNull Set<UnqueuedSlashCommandData> slashData, @NotNull Set<UnqueuedCommandData> commandData) {
		slashData.forEach(data -> jda.upsertCommand(data.getData()).queue());
		commandData.forEach(data -> jda.upsertCommand(data.getData()).queue());
	}

	/**
	 * Creates guild commands from the given (Slash-) CommandData
	 *
	 * @param guild       The {@link Guild}.
	 * @param slashData   A set of {@link SlashCommandData}.
	 * @param commandData A set of {@link CommandData},
	 */
	private void upsert(Guild guild, @NotNull Set<UnqueuedSlashCommandData> slashData, @NotNull Set<UnqueuedCommandData> commandData) {
		StringBuilder commandNames = new StringBuilder();
		slashData.forEach(data -> {
			if (data.getGuilds().contains(guild)) {
				guild.upsertCommand(data.getData()).queue();
				commandNames.append(", /").append(data.getData().getName());
			} else {
				DIH4JDALogger.info("Skipping Registration of /" + data.getData().getName() + " for Guild: " + guild.getName(), DIH4JDALogger.Type.SLASH_COMMAND_SKIPPED);
			}
		});
		commandData.forEach(data -> {
			if (data.getGuilds().contains(guild)) {
				guild.upsertCommand(data.getData()).queue();
				commandNames.append(", ").append(data.getData().getName());
			} else {
				DIH4JDALogger.info("Skipping Registration of " + data.getData().getName() + " for Guild: " + guild.getName(), DIH4JDALogger.Type.SLASH_COMMAND_SKIPPED);
			}
		});
		if (!commandNames.toString().isEmpty()) {
			DIH4JDALogger.info(String.format("Queued %s command(s) in guild %s: %s", slashData.size() + commandData.size(), guild.getName(),
					commandNames.substring(2)), DIH4JDALogger.Type.COMMANDS_QUEUED);
		}
	}

	/**
	 * Finds all Slash Commands using the {@link Reflections} API.
	 * Loops through all classes found in the commands package that is a subclass of
	 * {@link SlashCommand}.
	 */
	private Set<Class<? extends SlashCommand>> findSlashCommands() {
		Reflections classes = new Reflections(config.getCommandsPackage());
		return classes.getSubTypesOf(SlashCommand.class);
	}

	/**
	 * Finds all Context Commands using the {@link Reflections} API.
	 * Loops through all classes found in the commands package that is a subclass of
	 * {@link ContextCommand}.
	 */
	private Set<Class<? extends ContextCommand>> findContextCommands() {
		Reflections classes = new Reflections(config.getCommandsPackage());
		return classes.getSubTypesOf(ContextCommand.class);
	}

	/**
	 * Gets all Commands that were found in {@link InteractionHandler#findSlashCommands()} and adds
	 * them to the {@link InteractionHandler#slashCommandIndex}.
	 *
	 * @throws ReflectiveOperationException If an error occurs.
	 */
	private @NotNull Set<UnqueuedSlashCommandData> getSlashCommandData() throws ReflectiveOperationException {
		Set<UnqueuedSlashCommandData> data = new HashSet<>();
		for (Class<? extends SlashCommand> c : commands) {
			SlashCommand instance = (SlashCommand) ClassUtils.getInstance(c);
			if (instance != null) {
				UnqueuedSlashCommandData unqueuedData = new UnqueuedSlashCommandData(getBaseCommandData(instance, c), instance.getRegistrationType());
				if (instance.getRegistrationType() == RegistrationType.GUILD) {
					unqueuedData.setGuilds(instance.getGuilds(dih4jda.getConfig().getJDA()));
				}
				data.add(unqueuedData);
			}
		}
		return data;
	}

	/**
	 * Gets the complete {@link SlashCommandData} (including Subcommands & Subcommand Groups) of a single {@link SlashCommand}.
	 *
	 * @param command      The base command's instance.
	 * @param commandClass The base command's class.
	 * @return The new {@link CommandListUpdateAction}.
	 * @throws ReflectiveOperationException If an error occurs.
	 */
	private @Nullable SlashCommandData getBaseCommandData(@NotNull SlashCommand command, Class<? extends SlashCommand> commandClass) throws ReflectiveOperationException {
		// find component (and modal) handlers
		if (command.getSlashCommandData() == null) {
			DIH4JDALogger.warn(String.format("Class %s is missing CommandData. It will be ignored.", commandClass.getName()));
			return null;
		}
		SlashCommandData commandData = command.getSlashCommandData();
		if (command.getSubcommandGroups() != null && !command.getSubcommandGroups().isEmpty()) {
			commandData.addSubcommandGroups(getSubcommandGroupData(command));
		}
		if (command.getSubcommands() != null && !command.getSubcommands().isEmpty()) {
			commandData.addSubcommands(getSubcommandData(command, command.getSubcommands(), null));
		}
		if (command.getSubcommandGroups() != null && command.getSubcommandGroups().isEmpty()
				&& command.getSubcommands() != null && command.getSubcommands().isEmpty()) {
			slashCommandIndex.put(CommandUtils.buildCommandPath(commandData.getName()), command);
			DIH4JDALogger.info(String.format("\t[*] Registered command: /%s (%s)", command.getSlashCommandData().getName(), command.getRegistrationType().name()), DIH4JDALogger.Type.SLASH_COMMAND_REGISTERED);
			if (command.isAutoCompleteHandling() && Checks.checkImplementation(command.getClass(), AutoCompletable.class)) {
				autoCompleteIndex.put(commandData.getName(), (AutoCompletable) command);
			}
		}
		return commandData;
	}

	/**
	 * Gets all {@link SubcommandGroupData} (including Subcommands) of a single {@link SlashCommand}.
	 *
	 * @param command The base command's instance.
	 * @return All {@link SubcommandGroupData} stored in a List.
	 * @throws ReflectiveOperationException If an error occurs.
	 */
	private @NotNull Set<SubcommandGroupData> getSubcommandGroupData(@NotNull SlashCommand command) throws ReflectiveOperationException {
		Set<SubcommandGroupData> groupDataList = new HashSet<>();
		for (Map.Entry<SubcommandGroupData, Set<SlashCommand.Subcommand>> group : command.getSubcommandGroups().entrySet()) {
			if (group != null) {
				if (group.getKey() == null) {
					DIH4JDALogger.warn(String.format("Class %s is missing SubcommandGroupData. It will be ignored.", group.getClass().getSimpleName()));
					continue;
				}
				if (group.getValue() == null || group.getValue().isEmpty()) {
					DIH4JDALogger.warn(String.format("SubcommandGroup %s is missing Subcommands. It will be ignored.", group.getKey().getName()));
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
	 * @param subcommands   All sub command classes.
	 * @param subGroupName The Subcommand Group's name. (if available)
	 * @return The new {@link CommandListUpdateAction}.
	 * @throws ReflectiveOperationException If an error occurs.
	 */
	private @NotNull Set<SubcommandData> getSubcommandData(SlashCommand command, @NotNull Set<SlashCommand.Subcommand> subcommands, @Nullable String subGroupName) throws ReflectiveOperationException {
		Set<SubcommandData> subDataList = new HashSet<>();
		for (SlashCommand.Subcommand subcommand : subcommands) {
			if (subcommand != null) {
				if (subcommand.getSubcommandData() == null) {
					DIH4JDALogger.warn(String.format("Class %s is missing SubcommandData. It will be ignored.", subcommand.getClass().getSimpleName()));
					continue;
				}
				String commandPath;
				if (subGroupName == null) {
					commandPath = CommandUtils.buildCommandPath(command.getSlashCommandData().getName(), subcommand.getSubcommandData().getName());
				} else {
					commandPath = CommandUtils.buildCommandPath(command.getSlashCommandData().getName(), subGroupName, subcommand.getSubcommandData().getName());
				}
				subcommandIndex.put(commandPath, subcommand);
				DIH4JDALogger.info(String.format("\t[*] Registered command: /%s (%s)", commandPath, command.getRegistrationType().name()), DIH4JDALogger.Type.SLASH_COMMAND_REGISTERED);
				if (subcommand.isAutoCompleteHandling() && Checks.checkImplementation(subcommand.getClass(), AutoCompletable.class)) {
					autoCompleteIndex.put(commandPath, (AutoCompletable) subcommand);
				}
				subDataList.add(subcommand.getSubcommandData());
			}
		}
		return subDataList;
	}

	/**
	 * Gets all Guild Context commands registered in {@link InteractionHandler#findContextCommands()} and
	 * returns their {@link CommandData} as a List.
	 *
	 * @throws ReflectiveOperationException If an error occurs.
	 */
	private @NotNull Set<UnqueuedCommandData> getContextCommandData() throws ReflectiveOperationException {
		Set<UnqueuedCommandData> data = new HashSet<>();
		for (Class<? extends ContextCommand> c : contexts) {
			ContextCommand instance = (ContextCommand) ClassUtils.getInstance(c);
			if (instance != null) {
				UnqueuedCommandData unqueuedData = new UnqueuedCommandData(getContextCommandData(instance, c), instance.getRegistrationType());
				if (instance.getRegistrationType() == RegistrationType.GUILD) {
					unqueuedData.setGuilds(instance.getGuilds(dih4jda.getConfig().getJDA()));
				}
				data.add(unqueuedData);
			}
		}
		return data;
	}

	/**
	 * Gets the complete {@link CommandData} from a single {@link ContextCommand}.
	 *
	 * @param command      The base context command's instance.
	 * @param commandClass The base context command's class.
	 * @return The new {@link CommandListUpdateAction}.
	 */
	private @Nullable CommandData getContextCommandData(@NotNull ContextCommand command, Class<? extends ContextCommand> commandClass) {
		if (command.getCommandData() == null) {
			DIH4JDALogger.warn(String.format("Class %s is missing CommandData. It will be ignored.", commandClass.getName()));
			return null;
		}
		CommandData commandData = command.getCommandData();
		if (commandData.getType() == Command.Type.MESSAGE) {
			messageContextIndex.put(commandData.getName(), (ContextCommand.Message) command);
		} else if (commandData.getType() == Command.Type.USER) {
			userContextIndex.put(commandData.getName(), (ContextCommand.User) command);
		} else {
			DIH4JDALogger.error(String.format("Invalid Command Type \"%s\" for Context Command! This command will be ignored.", commandData.getType()));
			return null;
		}
		DIH4JDALogger.info(String.format("\t[*] Registered context command: %s (%s)", command.getCommandData().getName(), command.getRegistrationType().name()), DIH4JDALogger.Type.CONTEXT_COMMAND_REGISTERED);
		return commandData;
	}

	/**
	 * Handles a single {@link SlashCommand} or {@link SlashCommand.Subcommand}.
	 * If a {@link SlashCommandInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link SlashCommandInteractionEvent} that was fired.
	 */
	private void handleSlashCommand(@NotNull SlashCommandInteractionEvent event) throws Exception {
		String path = event.getCommandPath();
		CommandRequirements req = slashCommandIndex.containsKey(path) ? slashCommandIndex.get(path) : subcommandIndex.get(path);
		if (req == null && config.isThrowUnregisteredException()) {
			throw new CommandNotRegisteredException(String.format("Slash Command \"%s\" is not registered.", path));
		} else {
			if (!checkPermissions(event.getInteraction(), req.getRequiredPermissions())
					&& !checkUser(event.getInteraction(), req.getRequiredUsers())
					&& !checkRole(event, req.getRequiredRoles())) {
				if (slashCommandIndex.containsKey(event.getCommandPath())) {
					slashCommandIndex.get(path).execute(event);
				} else {
					subcommandIndex.get(path).execute(event);
				}
			}
		}
	}

	/**
	 * Handles a single {@link ContextCommand.User}.
	 * If a {@link UserContextInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link UserContextInteractionEvent} that was fired.
	 */
	private void handleUserContextCommand(@NotNull UserContextInteractionEvent event) throws Exception {
		ContextCommand.User context = userContextIndex.get(event.getCommandPath());
		if (context == null && config.isThrowUnregisteredException()) {
			throw new CommandNotRegisteredException(String.format("Context Command \"%s\" is not registered.", event.getCommandPath()));
		} else {
			if (!checkPermissions(event.getInteraction(), context.getRequiredPermissions())
					&& !checkUser(event.getInteraction(), context.getRequiredUsers())
					&& !checkRole(event.getInteraction(), context.getRequiredRoles())) {
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
	private void handleMessageContextCommand(@NotNull MessageContextInteractionEvent event) throws Exception {
		ContextCommand.Message context = messageContextIndex.get(event.getCommandPath());
		if (context == null && config.isThrowUnregisteredException()) {
			throw new CommandNotRegisteredException(String.format("Context Command \"%s\" is not registered.", event.getCommandPath()));
		} else {
			if (!checkPermissions(event.getInteraction(), context.getRequiredPermissions())
					&& !checkUser(event.getInteraction(), context.getRequiredUsers())
					&& !checkRole(event.getInteraction(), context.getRequiredRoles())) {
				context.execute(event);
			}
		}
	}

	/**
	 * Checks the user's permissions to fire the {@link DIH4JDAListenerAdapter#onInsufficientPermissions} event, if needed.
	 *
	 * @param interaction The {@link CommandInteraction}.
	 * @param permissions A set of {@link Permission}s.
	 * @return Whether the event was fired.
	 * @since v1.5
	 */
	private boolean checkPermissions(CommandInteraction interaction, @NotNull Set<Permission> permissions) {
		if (!permissions.isEmpty() && interaction.isFromGuild() && interaction.getMember() != null && !interaction.getMember().hasPermission(permissions)) {
			DIH4JDAListenerAdapter.fireEvent(dih4jda.getListeners(), "onInsufficientPermissions", interaction, permissions);
			return true;
		}
		return false;
	}

	/**
	 * Checks the user to fire the {@link DIH4JDAListenerAdapter#onInvalidUser} event, if needed.
	 *
	 * @param interaction The {@link CommandInteraction}.
	 * @param userIds     A set of {@link Long}s, representing the user ids.
	 * @return Whether the event was fired.
	 * @since v1.5
	 */
	private boolean checkUser(CommandInteraction interaction, @NotNull Set<Long> userIds) {
		if (!userIds.isEmpty() && !userIds.contains(interaction.getUser().getIdLong())) {
			DIH4JDAListenerAdapter.fireEvent(dih4jda.getListeners(), "onInvalidUser", interaction, userIds);
			return true;
		}
		return false;
	}

	private boolean checkRole(@NotNull CommandInteraction interaction, Set<Long> roleIds) {
		if (!interaction.isFromGuild() || interaction.getGuild() == null || interaction.getMember() == null) return false;
		Member member = interaction.getMember();
		if (!roleIds.isEmpty() && !member.getRoles().isEmpty() && member.getRoles().stream().noneMatch(r -> roleIds.contains(r.getIdLong()))) {
			DIH4JDAListenerAdapter.fireEvent(dih4jda.getListeners(), "onInvalidRole", interaction, roleIds);
			return true;
		}
		return false;
	}

	/**
	 * Fired if Discord reports a {@link SlashCommandInteractionEvent}.
	 *
	 * @param event The {@link SlashCommandInteractionEvent} that was fired.
	 */
	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		CompletableFuture.runAsync(() -> {
			try {
				handleSlashCommand(event);
			} catch (Exception e) {
				DIH4JDAListenerAdapter.fireEvent(dih4jda.getListeners(), "onCommandException", event.getInteraction(), e);
			}
		}, config.getExecutor());
	}

	/**
	 * Fired if Discord reports a {@link UserContextInteractionEvent}.
	 *
	 * @param event The {@link UserContextInteractionEvent} that was fired.
	 */
	@Override
	public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
		CompletableFuture.runAsync(() -> {
			try {
				handleUserContextCommand(event);
			} catch (Exception e) {
				DIH4JDAListenerAdapter.fireEvent(dih4jda.getListeners(), "onCommandException", event.getInteraction(), e);
			}
		}, config.getExecutor());
	}

	/**
	 * Fired if Discord reports a {@link MessageContextInteractionEvent}.
	 *
	 * @param event The {@link MessageContextInteractionEvent} that was fired.
	 */
	@Override
	public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
		CompletableFuture.runAsync(() -> {
			try {
				handleMessageContextCommand(event);
			} catch (Exception e) {
				DIH4JDAListenerAdapter.fireEvent(dih4jda.getListeners(), "onCommandException", event.getInteraction(), e);
			}
		}, config.getExecutor());
	}

	/**
	 * Fired if Discord reports a {@link CommandAutoCompleteInteractionEvent}.
	 *
	 * @param event The {@link CommandAutoCompleteInteractionEvent} that was fired.
	 */
	@Override
	public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
		CompletableFuture.runAsync(() -> {
			try {
				AutoCompletable autoComplete = autoCompleteIndex.get(event.getCommandPath());
				if (autoComplete != null) {
					autoComplete.handleAutoComplete(event, event.getFocusedOption());
				}
			} catch (Exception e) {
				DIH4JDAListenerAdapter.fireEvent(dih4jda.getListeners(), "onAutoCompleteException", event.getInteraction(), e);
			}
		}, config.getExecutor());
	}

	/**
	 * Fired if Discord reports a {@link ButtonInteractionEvent}.
	 *
	 * @param event The {@link ButtonInteractionEvent} that was fired.
	 */
	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
		CompletableFuture.runAsync(() -> {
			try {
				ButtonHandler button = dih4jda.getButtonHandlers().get(ComponentIdBuilder.split(event.getComponentId())[0]);
				if (button == null) {
					DIH4JDALogger.warn(String.format("Button with id \"%s\" could not be found.", event.getComponentId()), DIH4JDALogger.Type.BUTTON_NOT_FOUND);
				} else {
					button.handleButton(event, event.getButton());
				}
			} catch (Exception e) {
				DIH4JDAListenerAdapter.fireEvent(dih4jda.getListeners(), "onComponentException", event.getInteraction(), e);
			}
		}, config.getExecutor());
	}

	/**
	 * Fired if Discord reports a {@link SelectMenuInteractionEvent}.
	 *
	 * @param event The {@link SelectMenuInteractionEvent} that was fired.
	 */
	@Override
	public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
		CompletableFuture.runAsync(() -> {
			try {
				SelectMenuHandler selectMenu = dih4jda.getSelectMenuHandlers().get(ComponentIdBuilder.split(event.getComponentId())[0]);
				if (selectMenu == null) {
					DIH4JDALogger.warn(String.format("Select Menu with id \"%s\" could not be found.", event.getComponentId()), DIH4JDALogger.Type.SELECT_MENU_NOT_FOUND);
				} else {
					selectMenu.handleSelectMenu(event, event.getValues());
				}
			} catch (Exception e) {
				DIH4JDAListenerAdapter.fireEvent(dih4jda.getListeners(), "onComponentException", event.getInteraction(), e);
			}
		}, config.getExecutor());
	}

	/**
	 * Fired if Discord reports a {@link ModalInteractionEvent}.
	 *
	 * @param event The {@link ModalInteractionEvent} that was fired.
	 */
	@Override
	public void onModalInteraction(@NotNull ModalInteractionEvent event) {
		CompletableFuture.runAsync(() -> {
			try {
				ModalHandler modal = dih4jda.getModalHandlers().get(ComponentIdBuilder.split(event.getModalId())[0]);
				if (modal == null) {
					DIH4JDALogger.warn(String.format("Modal with id \"%s\" could not be found.", event.getModalId()), DIH4JDALogger.Type.MODAL_NOT_FOUND);
				} else {
					modal.handleModal(event, event.getValues());
				}
			} catch (Exception e) {
				DIH4JDAListenerAdapter.fireEvent(dih4jda.getListeners(), "onModalException", event.getInteraction(), e);
			}
		}, config.getExecutor());
	}
}
