package com.dynxsty.dih4jda;

import com.dynxsty.dih4jda.events.DIH4JDAListenerAdapter;
import com.dynxsty.dih4jda.exceptions.CommandNotRegisteredException;
import com.dynxsty.dih4jda.interactions.commands.*;
import com.dynxsty.dih4jda.interactions.commands.autocomplete.AutoCompleteHandler;
import com.dynxsty.dih4jda.interactions.ComponentIdBuilder;
import com.dynxsty.dih4jda.util.Checks;
import com.dynxsty.dih4jda.util.ClassUtils;
import com.dynxsty.dih4jda.util.CommandUtils;
import kotlin.Pair;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	 * An Index of all {@link AutoCompleteHandler}s.
	 *
	 * @see InteractionHandler#findSlashCommands()
	 */
	private final Map<String, AutoCompleteHandler> autoCompleteIndex;

	/**
	 * An Index of all {@link ComponentHandler}s.
	 *
	 * @see InteractionHandler#findInteractionsHandlers(ComponentHandler)
	 */
	private final Map<String, ComponentHandler> handlerIndex;

	private final Set<Class<? extends SlashCommand>> commands;
	private final Set<Class<? extends ContextCommand>> contexts;

	/**
	 * Constructs a new {@link InteractionHandler} from the supplied commands package.
	 *
	 * @param dih4jda The {@link DIH4JDA} instance.
	 */
	protected InteractionHandler(DIH4JDA dih4jda) {
		this.dih4jda = dih4jda;

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
		handlerIndex = new HashMap<>();
	}

	/**
	 * Finds and registers all interactions.
	 * This method can be accessed from the {@link DIH4JDA} instance.
	 * <br>This is automatically executed each time the {@link ListenerAdapter#onReady(ReadyEvent)} event is executed.
	 * (can be disabled using {@link DIH4JDABuilder#disableAutomaticCommandRegistration()})
	 *
	 * @throws ReflectiveOperationException If an error occurs.
	 */
	public void registerInteractions() throws ReflectiveOperationException {
		// register commands for each guild
		for (Guild guild : dih4jda.getJDA().getGuilds()) {
			Pair<Set<SlashCommandData>, Set<CommandData>> data = new Pair<>(getSlashCommandData(guild), getContextCommandData(guild));
			// check if smart queuing is enabled
			if (dih4jda.isSmartQueuing()) {
				data = SmartQueue.checkGuild(guild, data.component1(), data.component2());
			}
			// upsert all guild commands
			if (!data.component1().isEmpty() || !data.component2().isEmpty()) {
				upsert(guild, data.component1(), data.component2());
				DIH4JDALogger.info(String.format("Queued %s command(s) in guild %s: %s", data.component1().size() + data.component2().size(), guild.getName(),
						CommandUtils.getNames(data.component2(), data.component1())), DIH4JDALogger.Type.COMMANDS_QUEUED);
			}
		}
		Pair<Set<SlashCommandData>, Set<CommandData>> data = new Pair<>(getSlashCommandData(null), getContextCommandData(null));
		// check if smart queuing is enabled
		if (dih4jda.isSmartQueuing()) {
			data = SmartQueue.checkGlobal(dih4jda.getJDA(), data.component1(), data.component2());
		}
		// upsert all global commands
		if (!data.component1().isEmpty() || !data.component2().isEmpty()) {
			upsert(dih4jda.getJDA(), data.component1(), data.component2());
			DIH4JDALogger.info(String.format("Queued %s global command(s): %s", data.component1().size() + data.component2().size(),
					CommandUtils.getNames(data.component2(), data.component1())), DIH4JDALogger.Type.COMMANDS_QUEUED);
		}
	}

	/**
	 * Creates global commands from the given (Slash-) CommandData
	 *
	 * @param jda         The {@link JDA} instance.
	 * @param slashData   A set of {@link SlashCommandData}.
	 * @param commandData A set of {@link CommandData},
	 */
	private void upsert(JDA jda, Set<SlashCommandData> slashData, Set<CommandData> commandData) {
		slashData.forEach(data -> jda.upsertCommand(data).queue());
		commandData.forEach(data -> jda.upsertCommand(data).queue());
	}

	/**
	 * Creates guild commands from the given (Slash-) CommandData
	 *
	 * @param guild       The {@link Guild}.
	 * @param slashData   A set of {@link SlashCommandData}.
	 * @param commandData A set of {@link CommandData},
	 */
	private void upsert(Guild guild, Set<SlashCommandData> slashData, Set<CommandData> commandData) {
		slashData.forEach(data -> guild.upsertCommand(data).queue());
		commandData.forEach(data -> guild.upsertCommand(data).queue());
	}

	/**
	 * Finds all Slash Commands using the {@link Reflections} API.
	 * Loops through all classes found in the commands package that is a subclass of
	 * {@link SlashCommand}.
	 */
	private Set<Class<? extends SlashCommand>> findSlashCommands() {
		Reflections classes = new Reflections(dih4jda.getCommandsPackage());
		return classes.getSubTypesOf(SlashCommand.class);
	}

	/**
	 * Finds all Context Commands using the {@link Reflections} API.
	 * Loops through all classes found in the commands package that is a subclass of
	 * {@link ContextCommand}.
	 */
	private Set<Class<? extends ContextCommand>> findContextCommands() {
		Reflections classes = new Reflections(dih4jda.getCommandsPackage());
		return classes.getSubTypesOf(ContextCommand.class);
	}

	/**
	 * Finds all Interaction Handlers and adds them to their corresponding index.
	 *
	 * @param command The {@link ComponentHandler}.
	 */
	private void findInteractionsHandlers(ComponentHandler command) {
		command.getHandledButtonIds().forEach(s -> handlerIndex.put(s, command));
		command.getHandledSelectMenuIds().forEach(s -> handlerIndex.put(s, command));
		command.getHandledModalIds().forEach(s -> handlerIndex.put(s, command));
	}

	/**
	 * Gets all Commands that were found in {@link InteractionHandler#findSlashCommands()} and adds
	 * them to the {@link InteractionHandler#slashCommandIndex}.
	 *
	 * @param guild The command's guild.
	 * @throws ReflectiveOperationException If an error occurs.
	 */
	private Set<SlashCommandData> getSlashCommandData(@Nullable Guild guild) throws ReflectiveOperationException {
		Set<SlashCommandData> data = new HashSet<>();
		for (Class<? extends SlashCommand> c : commands) {
			SlashCommand instance = (SlashCommand) ClassUtils.getInstance(guild, c);
			if (instance == null || (guild != null && instance.getType() != ExecutableCommand.Type.GUILD)) continue;
			if (guild != null && !instance.getGuilds(guild.getJDA()).contains(guild)) {
				DIH4JDALogger.info("Skipping Registration of " + c.getSimpleName() + " for Guild: " + guild.getName(), DIH4JDALogger.Type.SLASH_COMMAND_SKIPPED);
			} else {
				data.add(getBaseCommandData(instance, c, guild));
			}
		}
		return data;
	}

	/**
	 * Gets the complete {@link SlashCommandData} (including Subcommands & Subcommand Groups) of a single {@link SlashCommand}.
	 *
	 * @param command      The base command's instance.
	 * @param commandClass The base command's class.
	 * @param guild        The current guild (if available)
	 * @return The new {@link CommandListUpdateAction}.
	 * @throws ReflectiveOperationException If an error occurs.
	 */
	private SlashCommandData getBaseCommandData(@NotNull SlashCommand command, Class<? extends SlashCommand> commandClass, @Nullable Guild guild) throws ReflectiveOperationException {
		// find component (and modal) handlers
		this.findInteractionsHandlers(command);
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
			slashCommandIndex.put(CommandUtils.buildCommandPath(commandData.getName()), command);
			DIH4JDALogger.info(String.format("\t[*] Registered command: /%s", command.getCommandData().getName()), DIH4JDALogger.Type.SLASH_COMMAND_REGISTERED);
			if (command.shouldHandleAutoComplete() && Checks.checkImplementation(command.getClass(), AutoCompleteHandler.class)) {
				autoCompleteIndex.put(commandData.getName(), (AutoCompleteHandler) command);
				DIH4JDALogger.info("\t\t[*] Enabled Auto Complete Handling", DIH4JDALogger.Type.HANDLE_AUTOCOMPLETE);
			}
		}
		return commandData;
	}

	/**
	 * Gets all {@link SubcommandGroupData} (including Subcommands) of a single {@link SlashCommand}.
	 *
	 * @param command The base command's instance.
	 * @param guild   The current guild (if available)
	 * @return All {@link SubcommandGroupData} stored in a List.
	 * @throws ReflectiveOperationException If an error occurs.
	 */
	private Set<SubcommandGroupData> getSubcommandGroupData(@NotNull SlashCommand command, @Nullable Guild guild) throws ReflectiveOperationException {
		Set<SubcommandGroupData> groupDataList = new HashSet<>();
		for (Class<? extends SlashCommand.SubcommandGroup> group : command.getSubcommandGroups()) {
			SlashCommand.SubcommandGroup instance = (SlashCommand.SubcommandGroup) ClassUtils.getInstance(guild, group);
			if (instance == null) continue;
			if (instance.getSubcommandGroupData() == null) {
				DIH4JDALogger.warn(String.format("Class %s is missing SubcommandGroupData. It will be ignored.", group.getName()));
				continue;
			}
			if (instance.getSubcommands() == null) {
				DIH4JDALogger.warn(String.format("SubcommandGroup %s is missing Subcommands. It will be ignored.", instance.getSubcommandGroupData().getName()));
				continue;
			}
			SubcommandGroupData groupData = instance.getSubcommandGroupData();
			groupData.addSubcommands(getSubcommandData(command, instance.getSubcommands(), groupData.getName(), guild));
			groupDataList.add(groupData);
		}
		return groupDataList;
	}

	/**
	 * Gets all {@link SubcommandData} from the given array of {@link SlashCommand.Subcommand} classes.
	 *
	 * @param command      The base command's instance.
	 * @param subClasses   All sub command classes.
	 * @param subGroupName The Subcommand Group's name. (if available)
	 * @param guild        The current guild (if available)
	 * @return The new {@link CommandListUpdateAction}.
	 * @throws ReflectiveOperationException If an error occurs.
	 */
	private Set<SubcommandData> getSubcommandData(SlashCommand command, Class<? extends SlashCommand.Subcommand>[] subClasses, @Nullable String subGroupName, @Nullable Guild guild) throws ReflectiveOperationException {
		Set<SubcommandData> subDataList = new HashSet<>();
		for (Class<? extends SlashCommand.Subcommand> sub : subClasses) {
			SlashCommand.Subcommand instance = (SlashCommand.Subcommand) ClassUtils.getInstance(guild, sub);
			if (instance == null) continue;
			if (instance.getSubcommandData() == null) {
				DIH4JDALogger.warn(String.format("Class %s is missing SubcommandData. It will be ignored.", sub.getName()));
				continue;
			}
			this.findInteractionsHandlers(instance);
			String commandPath;
			if (subGroupName == null) {
				commandPath = CommandUtils.buildCommandPath(command.getCommandData().getName(), instance.getSubcommandData().getName());
			} else {
				commandPath = CommandUtils.buildCommandPath(command.getCommandData().getName(), subGroupName, instance.getSubcommandData().getName());
			}
			subcommandIndex.put(commandPath, instance);
			DIH4JDALogger.info(String.format("\t[*] Registered command: /%s", commandPath), DIH4JDALogger.Type.SLASH_COMMAND_REGISTERED);
			if (instance.shouldHandleAutoComplete() && Checks.checkImplementation(instance.getClass(), AutoCompleteHandler.class)) {
				autoCompleteIndex.put(commandPath, (AutoCompleteHandler) instance);
				DIH4JDALogger.info("\t\t[*] Enabled Auto Complete Handling", DIH4JDALogger.Type.HANDLE_AUTOCOMPLETE);
			}
			subDataList.add(instance.getSubcommandData());
		}
		return subDataList;
	}

	/**
	 * Gets all Guild Context commands registered in {@link InteractionHandler#findContextCommands()} and
	 * returns their {@link CommandData} as a List.
	 *
	 * @param guild The context command's guild.
	 * @throws ReflectiveOperationException If an error occurs.
	 */
	private Set<CommandData> getContextCommandData(@Nullable Guild guild) throws ReflectiveOperationException {
		Set<CommandData> data = new HashSet<>();
		for (Class<? extends ContextCommand> c : contexts) {
			ContextCommand instance = (ContextCommand) ClassUtils.getInstance(guild, c);
			if (instance == null || (guild != null && instance.getType() != ExecutableCommand.Type.GUILD)) continue;
			if (guild != null && !instance.getGuilds(guild.getJDA()).contains(guild)) {
				DIH4JDALogger.info("Skipping Registration of " + c.getSimpleName() + " for Guild: " + guild.getName(), DIH4JDALogger.Type.CONTEXT_COMMAND_SKIPPED);
			} else {
				data.add(getContextCommandData(instance, c));
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
	private CommandData getContextCommandData(@NotNull ContextCommand command, Class<? extends ContextCommand> commandClass) {
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
		DIH4JDALogger.info(String.format("\t[*] Registered context command: %s", command.getCommandData().getName()), DIH4JDALogger.Type.CONTEXT_COMMAND_REGISTERED);
		return commandData;
	}

	/**
	 * Handles a single {@link SlashCommand} or {@link SlashCommand.Subcommand}.
	 * If a {@link SlashCommandInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link SlashCommandInteractionEvent} that was fired.
	 */
	private void handleSlashCommand(SlashCommandInteractionEvent event) throws Exception {
		String path = event.getCommandPath();
		CommandRequirements req = slashCommandIndex.containsKey(path) ? slashCommandIndex.get(path) : subcommandIndex.get(path);
		if (req == null) {
			throw new CommandNotRegisteredException(String.format("Slash Command \"%s\" is not registered.", path));
		} else {
			if (!checkPermissions(event.getInteraction(), req.getRequiredPermissions())
					&& !checkUser(event.getInteraction(), req.getRequiredUsers())) {
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
	private void handleUserContextCommand(UserContextInteractionEvent event) throws Exception {
		ContextCommand.User context = userContextIndex.get(event.getCommandPath());
		if (context == null) {
			throw new CommandNotRegisteredException(String.format("Context Command \"%s\" is not registered.", event.getCommandPath()));
		} else {
			if (!checkPermissions(event.getInteraction(), context.getRequiredPermissions())
					&& !checkUser(event.getInteraction(), context.getRequiredUsers())) {
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
	private void handleMessageContextCommand(MessageContextInteractionEvent event) throws Exception {
		ContextCommand.Message context = messageContextIndex.get(event.getCommandPath());
		if (context == null) {
			throw new CommandNotRegisteredException(String.format("Context Command \"%s\" is not registered.", event.getCommandPath()));
		} else {
			if (!checkPermissions(event.getInteraction(), context.getRequiredPermissions())
					&& !checkUser(event.getInteraction(), context.getRequiredUsers())) {
				context.execute(event);
			}
		}
	}

	/**
	 * Handles a single {@link CommandAutoCompleteInteractionEvent}.
	 * If a {@link CommandAutoCompleteInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link CommandAutoCompleteInteractionEvent} that was fired.
	 */
	private void handleAutoComplete(CommandAutoCompleteInteractionEvent event) {
		AutoCompleteHandler component = autoCompleteIndex.get(event.getCommandPath());
		if (component != null) {
			component.handleAutoComplete(event, event.getFocusedOption());
		}
	}

	/**
	 * Handles a single {@link ButtonInteractionEvent}.
	 * If a {@link ButtonInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link ButtonInteractionEvent} that was fired.
	 */
	private void handleButton(ButtonInteractionEvent event) {
		ComponentHandler component = handlerIndex.get(ComponentIdBuilder.split(event.getComponentId())[0]);
		if (component == null) {
			DIH4JDALogger.warn(String.format("Button with id \"%s\" could not be found.", event.getComponentId()), DIH4JDALogger.Type.BUTTON_NOT_FOUND);
		} else {
			component.handleButton(event, event.getButton());
		}
	}

	/**
	 * Handles a single {@link SelectMenuInteractionEvent}.
	 * If a {@link SelectMenuInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link SelectMenuInteractionEvent} that was fired.
	 */
	private void handleSelectMenu(SelectMenuInteractionEvent event) {
		ComponentHandler component = handlerIndex.get(ComponentIdBuilder.split(event.getComponentId())[0]);
		if (component == null) {
			DIH4JDALogger.warn(String.format("Select Menu with id \"%s\" could not be found.", event.getComponentId()), DIH4JDALogger.Type.SELECT_MENU_NOT_FOUND);
		} else {
			component.handleSelectMenu(event, event.getValues());
		}
	}

	/**
	 * Handles a single {@link ModalInteractionEvent}.
	 * If a {@link ModalInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link ModalInteractionEvent} that was fired.
	 */
	private void handleModal(ModalInteractionEvent event) {
		ComponentHandler modal = handlerIndex.get(ComponentIdBuilder.split(event.getModalId())[0]);
		if (modal == null) {
			DIH4JDALogger.warn(String.format("Modal with id \"%s\" could not be found.", event.getModalId()), DIH4JDALogger.Type.MODAL_NOT_FOUND);
		} else {
			modal.handleModal(event, event.getValues());
		}
	}

	/**
	 * Fires an event from the {@link DIH4JDAListenerAdapter}.
	 *
	 * @param listeners A set of all classes that extend the {@link DIH4JDAListenerAdapter}.
	 * @param name      The event's name.
	 * @param args      The event's arguments.
	 * @since v1.5
	 */
	private void fireEvent(Set<DIH4JDAListenerAdapter> listeners, String name, Object... args) {
		if (listeners.isEmpty()) {
			DIH4JDALogger.warn(String.format("%s was fired, but not handled (No listener registered) ", name), DIH4JDALogger.Type.EVENT_FIRED);
		}
		for (DIH4JDAListenerAdapter listener : listeners) {
			try {
				for (Method method : listener.getClass().getMethods()) {
					if (method.getName().equals(name)) {
						method.invoke(listener.getClass().getConstructor().newInstance(), args);
					}
				}
			} catch (ReflectiveOperationException e) {
				DIH4JDALogger.error(e.getMessage());
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
	private boolean checkPermissions(CommandInteraction interaction, Set<Permission> permissions) {
		if (!permissions.isEmpty() && interaction.isFromGuild() && interaction.getMember() != null && !interaction.getMember().hasPermission(permissions)) {
			fireEvent(dih4jda.getListeners(), "onInsufficientPermissions", interaction, permissions);
			return true;
		}
		return false;
	}

	/**
	 * Checks the user to fire the {@link DIH4JDAListenerAdapter#onUserNotAllowed} event, if needed.
	 *
	 * @param interaction The {@link CommandInteraction}.
	 * @param userIds     A set of {@link Long}s, representing the user ids.
	 * @return Whether the event was fired.
	 * @since v1.5
	 */
	private boolean checkUser(CommandInteraction interaction, Set<Long> userIds) {
		if (!userIds.isEmpty() && !userIds.contains(interaction.getUser().getIdLong())) {
			fireEvent(dih4jda.getListeners(), "onUserNotAllowed", interaction, userIds);
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
				fireEvent(dih4jda.getListeners(), "onCommandException", event.getInteraction(), e);
			}
		}, dih4jda.getExecutor());
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
				fireEvent(dih4jda.getListeners(), "onCommandException", event.getInteraction(), e);
			}
		}, dih4jda.getExecutor());
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
				fireEvent(dih4jda.getListeners(), "onCommandException", event.getInteraction(), e);
			}
		}, dih4jda.getExecutor());
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
				handleAutoComplete(event);
			} catch (Exception e) {
				fireEvent(dih4jda.getListeners(), "onAutoCompleteException", event.getInteraction(), e);
			}
		}, dih4jda.getExecutor());
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
				handleButton(event);
			} catch (Exception e) {
				fireEvent(dih4jda.getListeners(), "onComponentException", event.getInteraction(), e);
			}
		}, dih4jda.getExecutor());
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
				handleSelectMenu(event);
			} catch (Exception e) {
				fireEvent(dih4jda.getListeners(), "onComponentException", event.getInteraction(), e);
			}
		}, dih4jda.getExecutor());
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
				handleModal(event);
			} catch (Exception e) {
				fireEvent(dih4jda.getListeners(), "onModalException", event.getInteraction(), e);
			}
		}, dih4jda.getExecutor());
	}
}
