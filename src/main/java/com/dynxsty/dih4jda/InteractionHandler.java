package com.dynxsty.dih4jda;

import com.dynxsty.dih4jda.exceptions.CommandNotRegisteredException;
import com.dynxsty.dih4jda.interactions.commands.ComponentHandler;
import com.dynxsty.dih4jda.interactions.commands.ContextCommand;
import com.dynxsty.dih4jda.interactions.commands.SlashCommand;
import com.dynxsty.dih4jda.interactions.commands.SlashCommandHandler;
import com.dynxsty.dih4jda.interactions.commands.autocomplete.AutoCompleteHandler;
import com.dynxsty.dih4jda.interactions.components.ComponentIdBuilder;
import com.dynxsty.dih4jda.interactions.components.button.ButtonHandler;
import com.dynxsty.dih4jda.interactions.modal.ModalHandler;
import com.dynxsty.dih4jda.interactions.components.select_menu.SelectMenuHandler;
import com.dynxsty.dih4jda.util.Checks;
import com.dynxsty.dih4jda.util.ClassUtils;
import com.dynxsty.dih4jda.util.CommandUtils;
import kotlin.Pair;
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
	 * An Index of all Slash Command Interactions.
	 *
	 * @see InteractionHandler#findSlashCommands()
	 */
	private final Map<String, SlashCommandHandler> slashCommandIndex;

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
	 * An Index of all {@link ButtonHandler}s.
	 *
	 * @see InteractionHandler#findInteractionsHandlers(ComponentHandler)
	 */
	private final Map<String, ButtonHandler> buttonIndex;

	/**
	 * An Index of all {@link SelectMenuHandler}s.
	 *
	 * @see InteractionHandler#findInteractionsHandlers(ComponentHandler)
	 */
	private final Map<String, SelectMenuHandler> selectMenuIndex;

	/**
	 * An Index of all {@link ModalHandler}s.
	 *
	 * @see InteractionHandler#findInteractionsHandlers(ComponentHandler)
	 */
	private final Map<String, ModalHandler> modalIndex;

	private final Set<Class<? extends SlashCommand>> commands;
	private final Set<Class<? extends ContextCommand>> contexts;

	/**
	 * Constructs a new {@link InteractionHandler} from the supplied commands package.
	 *
	 * @param dih4jda The {@link DIH4JDA} instance.
	 */
	protected InteractionHandler(DIH4JDA dih4jda) {
		this.commands = new HashSet<>();
		this.contexts = new HashSet<>();

		this.slashCommandIndex = new HashMap<>();
		this.messageContextIndex = new HashMap<>();
		this.userContextIndex = new HashMap<>();
		this.autoCompleteIndex = new HashMap<>();
		this.buttonIndex = new HashMap<>();
		this.selectMenuIndex = new HashMap<>();
		this.modalIndex = new HashMap<>();
		this.dih4jda = dih4jda;
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
		// find all commands
		findSlashCommands();
		findContextCommands();
		// register commands for each guild
		for (Guild guild : dih4jda.getJDA().getGuilds()) {
			Pair<Set<SlashCommandData>, Set<CommandData>> data = new Pair<>(getSlashCommandData(guild), getContextCommandData(guild));
			if (dih4jda.isSmartQueuing()) {
				data = SmartQueue.queueGuild(guild, data.component1(), data.component2());
			}
			if (!data.component1().isEmpty() || !data.component2().isEmpty()) {
				guild.updateCommands()
						.addCommands(data.component1())
						.addCommands(data.component2())
						.queue();
				DIH4JDALogger.info(String.format("Queued %s new command(s) in guild %s: %s", data.component1().size() + data.component2().size(), guild.getName(),
						CommandUtils.getNames(data.component2(), data.component1())), DIH4JDALogger.Type.COMMANDS_QUEUED);
			}
		}
		Pair<Set<SlashCommandData>, Set<CommandData>> data = new Pair<>(getSlashCommandData(null), getContextCommandData(null));
		// check if smart queuing was disabled
		if (dih4jda.isSmartQueuing()) {
			data = SmartQueue.queueGlobal(dih4jda.getJDA(), data.component1(), data.component2());
		}
		// queue all global commands
		if (!data.component1().isEmpty() || !data.component2().isEmpty()) {
			dih4jda.getJDA().updateCommands()
					.addCommands(data.component1())
					.addCommands(data.component2())
					.queue();
			DIH4JDALogger.info(String.format("Queued %s new global command(s): %s", data.component1().size() + data.component2().size(),
					CommandUtils.getNames(data.component2(), data.component1())), DIH4JDALogger.Type.COMMANDS_QUEUED);
		}
	}

	/**
	 * Finds all Slash Commands using {@link Reflections}.
	 * Loops through all classes found in the commands package that is a subclass of
	 * {@link SlashCommand}.
	 */
	private void findSlashCommands() {
		Reflections classes = new Reflections(dih4jda.getCommandsPackage());
		commands.addAll(classes.getSubTypesOf(SlashCommand.class));
	}

	/**
	 * Finds all Context Commands using {@link Reflections}.
	 * Loops through all classes found in the commands package that is a subclass of
	 * {@link ContextCommand}.
	 */
	private void findContextCommands() {
		Reflections classes = new Reflections(dih4jda.getCommandsPackage());
		contexts.addAll(classes.getSubTypesOf(ContextCommand.class));
		contexts.removeAll(List.of(ContextCommand.User.class, ContextCommand.Message.class));
	}

	/**
	 * Finds all Interaction Handlers and adds them to their corresponding index.
	 *
	 * @param command The {@link ComponentHandler}.
	 */
	private void findInteractionsHandlers(ComponentHandler command) {
		command.getHandledButtonIds().forEach(s -> this.buttonIndex.put(s, (ButtonHandler) command));
		command.getHandledSelectMenuIds().forEach(s -> this.selectMenuIndex.put(s, (SelectMenuHandler) command));
		command.getHandledModalIds().forEach(s -> this.modalIndex.put(s, (ModalHandler) command));
	}

	/**
	 * Gets all Guild commands registered in {@link InteractionHandler#findSlashCommands()} and adds
	 * them to the {@link InteractionHandler#slashCommandIndex}.
	 *
	 * @param guild The command's guild.
	 * @throws ReflectiveOperationException If an error occurs.
	 */
	private Set<SlashCommandData> getSlashCommandData(@Nullable Guild guild) throws ReflectiveOperationException {
		Set<SlashCommandData> data = new HashSet<>();
		for (Class<? extends SlashCommand> c : commands) {
			SlashCommand instance = (SlashCommand) ClassUtils.getInstance(guild, c);
			if (instance == null || guild == null && instance.isGuildCommand()) continue;
			if (guild != null && !instance.getGuilds(guild.getJDA()).contains(guild)) {
				DIH4JDALogger.info("Skipping Registration of " + c.getSimpleName() + " for Guild: " + guild.getName(), DIH4JDALogger.Type.SLASH_COMMAND_SKIPPED);
			} else data.add(getBaseCommandData(instance, c, guild));
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
			slashCommandIndex.put(commandPath, instance);
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
			if (instance == null || guild == null && instance.isGuildCommand()) continue;
			if (guild != null && !instance.getGuilds(guild.getJDA()).contains(guild)) {
				DIH4JDALogger.info("Skipping Registration of " + c.getSimpleName() + " for Guild: " + guild.getName(), DIH4JDALogger.Type.CONTEXT_COMMAND_SKIPPED);
			} else data.add(getContextCommandData(instance, c));
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
	 * Handles a single {@link SlashCommand}.
	 * If a {@link SlashCommandInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link SlashCommandInteractionEvent} that was fired.
	 */
	private void handleSlashCommand(SlashCommandInteractionEvent event) {
		try {
			SlashCommandHandler command = slashCommandIndex.get(event.getCommandPath());
			if (command == null) {
				throw new CommandNotRegisteredException(String.format("Slash Command \"%s\" is not registered.", event.getCommandPath()));
			} else {
				command.execute(event);
			}
		} catch (Exception e) {
			DIH4JDALogger.error(String.format("A %s was raised while handling a Slash Command: %s", e.getClass().getSimpleName(), e.getMessage()), DIH4JDALogger.Type.COMMAND_EXCEPTION);
		}
	}

	/**
	 * Handles a single {@link ContextCommand.User}.
	 * If a {@link UserContextInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link UserContextInteractionEvent} that was fired.
	 */
	private void handleUserContextCommand(UserContextInteractionEvent event) {
		try {
			ContextCommand.User context = userContextIndex.get(event.getCommandPath());
			if (context == null) {
				throw new CommandNotRegisteredException(String.format("Context Command \"%s\" is not registered.", event.getCommandPath()));
			} else {
				context.execute(event);
			}
		} catch (Exception e) {
			DIH4JDALogger.error(String.format("A %s was raised while handling a User Context Command: %s", e.getClass().getSimpleName(), e.getMessage()), DIH4JDALogger.Type.COMMAND_EXCEPTION);
		}
	}

	/**
	 * Handles a single {@link ContextCommand.Message}.
	 * If a {@link MessageContextInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link MessageContextInteractionEvent} that was fired.
	 */
	private void handleMessageContextCommand(MessageContextInteractionEvent event) {
		try {
			ContextCommand.Message context = messageContextIndex.get(event.getCommandPath());
			if (context == null) {
				throw new CommandNotRegisteredException(String.format("Context Command \"%s\" is not registered.", event.getCommandPath()));
			} else {
				context.execute(event);
			}
		} catch (Exception e) {
			DIH4JDALogger.error(String.format("A %s was raised while handling a Message Context Command: %s", e.getClass().getSimpleName(), e.getMessage()), DIH4JDALogger.Type.COMMAND_EXCEPTION);
		}
	}

	/**
	 * Handles a single {@link CommandAutoCompleteInteractionEvent}.
	 * If a {@link CommandAutoCompleteInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link CommandAutoCompleteInteractionEvent} that was fired.
	 */
	private void handleAutoComplete(CommandAutoCompleteInteractionEvent event) {
		try {
			AutoCompleteHandler component = autoCompleteIndex.get(event.getCommandPath());
			if (component != null) {
				component.handleAutoComplete(event, event.getFocusedOption());
			}
		} catch (Exception e) {
			DIH4JDALogger.error(String.format("A %s was raised while handling an AutoComplete Interaction: %s", e.getClass().getSimpleName(), e.getMessage()), DIH4JDALogger.Type.COMMAND_EXCEPTION);
		}
	}

	/**
	 * Handles a single {@link ButtonInteractionEvent}.
	 * If a {@link ButtonInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link ButtonInteractionEvent} that was fired.
	 */
	private void handleButton(ButtonInteractionEvent event) {
		try {
			ButtonHandler component = buttonIndex.get(ComponentIdBuilder.split(event.getComponentId())[0]);
			if (component == null) {
				DIH4JDALogger.warn(String.format("Button with id \"%s\" could not be found.", event.getComponentId()), DIH4JDALogger.Type.BUTTON_NOT_FOUND);
			} else {
				component.handleButton(event, event.getButton());
			}
		} catch (Exception e) {
			DIH4JDALogger.error(String.format("A %s was raised while handling a Button Interaction: %s", e.getClass().getSimpleName(), e.getMessage()), DIH4JDALogger.Type.COMMAND_EXCEPTION);
		}
	}

	/**
	 * Handles a single {@link SelectMenuInteractionEvent}.
	 * If a {@link SelectMenuInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link SelectMenuInteractionEvent} that was fired.
	 */
	private void handleSelectMenu(SelectMenuInteractionEvent event) {
		try {
			SelectMenuHandler component = selectMenuIndex.get(ComponentIdBuilder.split(event.getComponentId())[0]);
			if (component == null) {
				DIH4JDALogger.warn(String.format("Select Menu with id \"%s\" could not be found.", event.getComponentId()), DIH4JDALogger.Type.SELECT_MENU_NOT_FOUND);
			} else {
				component.handleSelectMenu(event, event.getValues());
			}
		} catch (Exception e) {
			DIH4JDALogger.error(String.format("A %s was raised while handling a Select Menu Interaction: %s", e.getClass().getSimpleName(), e.getMessage()), DIH4JDALogger.Type.COMMAND_EXCEPTION);
		}
	}

	/**
	 * Handles a single {@link ModalInteractionEvent}.
	 * If a {@link ModalInteractionEvent} is fired the corresponding class is found and the command is executed.
	 *
	 * @param event The {@link ModalInteractionEvent} that was fired.
	 */
	private void handleModal(ModalInteractionEvent event) {
		try {
			ModalHandler modal = modalIndex.get(ComponentIdBuilder.split(event.getModalId())[0]);
			if (modal == null) {
				DIH4JDALogger.warn(String.format("Modal with id \"%s\" could not be found.", event.getModalId()), DIH4JDALogger.Type.MODAL_NOT_FOUND);
			} else {
				modal.handleModal(event, event.getValues());
			}
		} catch (Exception e) {
			DIH4JDALogger.error(String.format("A %s was raised while handling a Modal Interaction: %s", e.getClass().getSimpleName(), e.getMessage()), DIH4JDALogger.Type.COMMAND_EXCEPTION);
		}
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

	/**
	 * Fired if Discord reports a {@link CommandAutoCompleteInteractionEvent}.
	 *
	 * @param event The {@link CommandAutoCompleteInteractionEvent} that was fired.
	 */
	@Override
	public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
		CompletableFuture.runAsync(() -> this.handleAutoComplete(event));
	}

	/**
	 * Fired if Discord reports a {@link ButtonInteractionEvent}.
	 *
	 * @param event The {@link ButtonInteractionEvent} that was fired.
	 */
	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
		CompletableFuture.runAsync(() -> this.handleButton(event));
	}

	/**
	 * Fired if Discord reports a {@link SelectMenuInteractionEvent}.
	 *
	 * @param event The {@link SelectMenuInteractionEvent} that was fired.
	 */
	@Override
	public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
		CompletableFuture.runAsync(() -> this.handleSelectMenu(event));
	}

	/**
	 * Fired if Discord reports a {@link ModalInteractionEvent}.
	 *
	 * @param event The {@link ModalInteractionEvent} that was fired.
	 */
	@Override
	public void onModalInteraction(@NotNull ModalInteractionEvent event) {
		CompletableFuture.runAsync(() -> this.handleModal(event));
	}
}
