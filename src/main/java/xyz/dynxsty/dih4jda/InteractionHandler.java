package xyz.dynxsty.dih4jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import xyz.dynxsty.dih4jda.config.DIH4JDAConfig;
import xyz.dynxsty.dih4jda.events.AutoCompleteExceptionEvent;
import xyz.dynxsty.dih4jda.events.CommandCooldownEvent;
import xyz.dynxsty.dih4jda.events.CommandExceptionEvent;
import xyz.dynxsty.dih4jda.events.ComponentExceptionEvent;
import xyz.dynxsty.dih4jda.events.DIH4JDAEvent;
import xyz.dynxsty.dih4jda.events.InsufficientPermissionsEvent;
import xyz.dynxsty.dih4jda.events.InvalidGuildEvent;
import xyz.dynxsty.dih4jda.events.InvalidRoleEvent;
import xyz.dynxsty.dih4jda.events.InvalidUserEvent;
import xyz.dynxsty.dih4jda.events.ModalExceptionEvent;
import xyz.dynxsty.dih4jda.exceptions.CommandNotRegisteredException;
import xyz.dynxsty.dih4jda.exceptions.DIH4JDAException;
import xyz.dynxsty.dih4jda.interactions.AutoCompletable;
import xyz.dynxsty.dih4jda.interactions.commands.RestrictedCommand;
import xyz.dynxsty.dih4jda.interactions.commands.application.BaseApplicationCommand;
import xyz.dynxsty.dih4jda.interactions.commands.application.ContextCommand;
import xyz.dynxsty.dih4jda.interactions.commands.application.RegistrationType;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;
import xyz.dynxsty.dih4jda.interactions.components.ButtonHandler;
import xyz.dynxsty.dih4jda.interactions.components.EntitySelectMenuHandler;
import xyz.dynxsty.dih4jda.interactions.components.IdMapping;
import xyz.dynxsty.dih4jda.interactions.components.ModalHandler;
import xyz.dynxsty.dih4jda.interactions.components.StringSelectMenuHandler;
import xyz.dynxsty.dih4jda.util.Checks;
import xyz.dynxsty.dih4jda.util.ClassUtils;
import xyz.dynxsty.dih4jda.util.ClassWalker;
import xyz.dynxsty.dih4jda.util.CommandUtils;
import xyz.dynxsty.dih4jda.util.ComponentIdBuilder;
import xyz.dynxsty.dih4jda.util.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Handler class, that finds, registers and handles all Application Commands and their components.
 *
 * @see DIH4JDABuilder#disableAutomaticCommandRegistration()
 * @see DIH4JDA#registerInteractions()
 */
public class InteractionHandler extends ListenerAdapter {

    /**
     * Cache for all retrieved (and/or queued) commands.
     */
    private static final Map<String, Command> RETRIEVED_COMMANDS;

    static {
        RETRIEVED_COMMANDS = new HashMap<>();
    }

    protected final Set<SlashCommand> slashCommands;
    protected final Set<ContextCommand<?>> contextCommands;
    /**
     * The main {@link DIH4JDA} instance.
     */
    private final DIH4JDA dih4jda;
    /**
     * The instance's {@link DIH4JDAConfig configuration}.
     */
    private final DIH4JDAConfig config;
    /**
     * An {@link Map} of all {@link SlashCommand}s with their {@link SlashCommandInteractionEvent#getFullCommandName name} as their key.
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

    /**
     * Constructs a new {@link InteractionHandler} from the supplied {@link DIH4JDA} instance}.
     *
     * @param dih4jda The {@link DIH4JDA} instance.
     */
    protected InteractionHandler(@Nonnull DIH4JDA dih4jda) {
        this.dih4jda = dih4jda;
        config = dih4jda.getConfig();

        slashCommands = new HashSet<>();
        contextCommands = new HashSet<>();
        for (String pkg : config.getCommandsPackages()) {
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
     * Returns an unmodifiable Map of all retrieved (and/or queued) commands, where the key is the commands' name and
     * the value the {@link Command} instance itself.
     * This map is empty if {@link DIH4JDA#registerInteractions()} wasn't called before.
     *
     * @return An immutable {@link Map} containing all global and guild commands.
     */
    @Nonnull
    public static Map<String, Command> getRetrievedCommands() {
        return Collections.unmodifiableMap(RETRIEVED_COMMANDS);
    }

    /**
     * Registers all interactions.
     * This method can be accessed from the {@link DIH4JDA} instance.
     * <br>This is automatically executed each time the {@link ListenerAdapter#onReady(net.dv8tion.jda.api.events.session.ReadyEvent)} event is executed.
     * (can be disabled using {@link DIH4JDABuilder#disableAutomaticCommandRegistration()})
     */
    public void registerInteractions() {
        // retrieve (and smartqueue) guild commands
        Pair<Set<SlashCommand>, Set<ContextCommand<?>>> data = new Pair<>(getSlashCommands(), getContextCommandData());
        for (Guild guild : config.getJda().getGuilds()) {
            guild.retrieveCommands(true).queue(existing -> {
                Pair<Set<SlashCommand>, Set<ContextCommand<?>>> guildData = CommandUtils.filterByType(data, RegistrationType.GUILD);
                existing.forEach(this::cacheCommand);
                // check if smart queuing is enabled
                if (config.isGuildSmartQueue()) {
                    guildData = new SmartQueue(guildData.getFirst(), guildData.getSecond(), config.isDeleteUnknownCommands()).checkGuild(guild, existing);
                }
                // upsert all (remaining) guild commands
                if (!guildData.getFirst().isEmpty() || !guildData.getSecond().isEmpty()) {
                    upsert(guild, guildData.getFirst(), guildData.getSecond());
                }
            }, error -> DIH4JDALogger.error("Could not retrieve commands for guild %s!" +
                    " Please make sure that the bot was invited with the application.commands scope!", guild.getName()));
        }
        // retrieve (and smartqueue) global commands
        config.getJda().retrieveCommands(true).queue(existing -> {
            Pair<Set<SlashCommand>, Set<ContextCommand<?>>> globalData = CommandUtils.filterByType(data, RegistrationType.GLOBAL);
            existing.forEach(this::cacheCommand);
            // check if smart queuing is enabled
            if (config.isGlobalSmartQueue()) {
                globalData = new SmartQueue(globalData.getFirst(), globalData.getSecond(), config.isDeleteUnknownCommands()).checkGlobal(existing);
            }
            // upsert all (remaining) global commands
            if (!globalData.getFirst().isEmpty() || !globalData.getSecond().isEmpty()) {
                upsert(config.getJda(), globalData.getFirst(), globalData.getSecond());
                DIH4JDALogger.info(DIH4JDALogger.Type.COMMANDS_QUEUED, "Queued %s global command(s): %s",
                        globalData.getFirst().size() + globalData.getSecond().size(), CommandUtils.getNames(globalData.getSecond(), globalData.getFirst()));
            }
        }, error -> DIH4JDALogger.error("Could not retrieve global commands!"));
        // Log autocomplete bindings
        if (!autoCompleteIndex.isEmpty()) {
            // print autocomplete bindings
            DIH4JDALogger.info("Created %s AutoComplete binding(s): %s", autoCompleteIndex.size(),
                    autoCompleteIndex.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue().getClass().getSimpleName()).collect(Collectors.joining(", ")));
        }
    }

    /**
     * Uses the provided {@link Set} of {@link SlashCommand} and {@link ContextCommand ContextCommandData}
     * and updates them globally, using the {@link JDA} instance.
     *
     * @param jda             The {@link JDA} instance.
     * @param slashCommands   A {@link Set} of {@link SlashCommand}.
     * @param contextCommands A {@link Set} of {@link ContextCommand}.
     * @since v1.7
     */
    private void update(@Nonnull JDA jda, @Nonnull Set<SlashCommand> slashCommands, @Nonnull Set<ContextCommand<?>> contextCommands) {
        jda.updateCommands()
                .addCommands(slashCommands.stream().map(SlashCommand::getCommandData).collect(Collectors.toSet()))
                .addCommands(contextCommands.stream().map(ContextCommand::getCommandData).collect(Collectors.toSet()))
                .queue(comdList -> comdList.forEach(this::cacheCommand));
    }

    /**
     * Uses the provided {@link Set} of {@link SlashCommand} and {@link ContextCommand ContextCommandData}
     * and queues them, using the specified {@link Guild} instance.
     * This also checks for {@link BaseApplicationCommand#getQueueableGuilds queueable guilds} and skips them if needed.
     *
     * @param guild           The {@link Guild}.
     * @param slashCommands   A {@link Set} of {@link SlashCommand}.
     * @param contextCommands A {@link Set} of {@link ContextCommand}.
     */
    private void update(@Nonnull Guild guild, @Nonnull Set<SlashCommand> slashCommands, @Nonnull Set<ContextCommand<?>> contextCommands) {
        Set<SlashCommand> queueableSlashCommands = slashCommands.stream()
                .filter(cmd -> CommandUtils.shouldBeRegistered(guild, cmd)).collect(Collectors.toSet());
        Set<ContextCommand<?>> queueableContextCommands = contextCommands.stream()
                .filter(cmd -> CommandUtils.shouldBeRegistered(guild, cmd)).collect(Collectors.toSet());
        guild.updateCommands()
                .addCommands(queueableSlashCommands.stream().map(SlashCommand::getCommandData).collect(Collectors.toSet()))
                .addCommands(queueableContextCommands.stream().map(ContextCommand::getCommandData).collect(Collectors.toSet()))
                .queue(comdList -> comdList.forEach(this::cacheCommand));

        if (queueableSlashCommands.size() != 0 || queueableContextCommands.size() != 0) {
            List<String> commandNames = queueableSlashCommands.stream()
                    .map(cmd -> cmd.getCommandData().getName()).collect(Collectors.toList());
            commandNames.addAll(queueableContextCommands.stream().map(cmd -> cmd.getCommandData().getName()).collect(Collectors.toList()));
            String commandNameStr = commandNames.toString();
            commandNameStr = commandNameStr.substring(1, commandNameStr.length() - 1);
            DIH4JDALogger.info(DIH4JDALogger.Type.COMMANDS_QUEUED, "Queued %s command(s) in guild %s: %s",
                    slashCommands.size() + contextCommands.size(), guild.getName(), commandNameStr);
        }
    }

    /**
     * Uses the provided {@link Set} of {@link SlashCommand} and {@link ContextCommand ContextCommandData}
     * and queues them globally, using the {@link JDA} instance.
     *
     * @param jda             The {@link JDA} instance.
     * @param slashCommands   A {@link Set} of {@link SlashCommand}.
     * @param contextCommands A {@link Set} of {@link ContextCommand}.
     */
    private void upsert(@Nonnull JDA jda, @Nonnull Set<SlashCommand> slashCommands, @Nonnull Set<ContextCommand<?>> contextCommands) {
        if (config.isDeleteUnknownCommands()) {
            update(jda, slashCommands, contextCommands);
        } else {
            slashCommands.forEach(data -> jda.upsertCommand(data.getCommandData()).queue(this::cacheCommand));
            contextCommands.forEach(data -> jda.upsertCommand(data.getCommandData()).queue(this::cacheCommand));
        }
    }

    /**
     * Uses the provided {@link Set} of {@link SlashCommand} and {@link ContextCommand ContextCommandData}
     * and queues them, using the specified {@link Guild} instance.
     * This also checks for {@link BaseApplicationCommand#getQueueableGuilds queueable guilds} and skips them if needed.
     *
     * @param guild           The {@link Guild}.
     * @param slashCommands   A {@link Set} of {@link SlashCommand}.
     * @param contextCommands A {@link Set} of {@link ContextCommand}.
     */
    private void upsert(@Nonnull Guild guild, @Nonnull Set<SlashCommand> slashCommands, @Nonnull Set<ContextCommand<?>> contextCommands) {
        if (config.isDeleteUnknownCommands()) {
            update(guild, slashCommands, contextCommands);
        } else {
            StringBuilder commandNames = new StringBuilder();
            slashCommands.forEach(data -> {
                if (CommandUtils.shouldBeRegistered(guild, data)) {
                    guild.upsertCommand(data.getCommandData()).queue(this::cacheCommand);
                    commandNames.append(", /").append(data.getCommandData().getName());
                }
            });
            contextCommands.forEach(data -> {
                if (CommandUtils.shouldBeRegistered(guild, data)) {
                    guild.upsertCommand(data.getCommandData()).queue(this::cacheCommand);
                    commandNames.append(", ").append(data.getCommandData().getName());
                }
            });
            if (!commandNames.toString().isEmpty()) {
                DIH4JDALogger.info(DIH4JDALogger.Type.COMMANDS_QUEUED, "Queued %s command(s) in guild %s: %s",
                        slashCommands.size() + contextCommands.size(), guild.getName(), commandNames.substring(2));
            }
        }
    }

    private void cacheCommand(@Nonnull Command command) {
        RETRIEVED_COMMANDS.put(command.getName(), command);
    }

    /**
     * Finds all Slash Commands using the {@link ClassWalker}.
     * Loops through all classes found in the commands package that is a subclass of
     * {@link SlashCommand}.
     */
    private void findSlashCommands(@Nonnull String pkg) throws ReflectiveOperationException, DIH4JDAException {
        ClassWalker classes = new ClassWalker(pkg);
        Set<Class<? extends SlashCommand>> subTypes = classes.getSubTypesOf(SlashCommand.class);
        for (Class<? extends SlashCommand> subType : subTypes) {
            if (Checks.checkEmptyConstructor(subType)) {
                slashCommands.add((SlashCommand) ClassUtils.getInstance(subType));
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
    private void findContextCommands(@Nonnull String pkg) throws ReflectiveOperationException, DIH4JDAException {
        ClassWalker classes = new ClassWalker(pkg);
        Set<Class<? extends ContextCommand>> subTypes = classes.getSubTypesOf(ContextCommand.class);
        for (Class<? extends ContextCommand> subType : subTypes) {
            if (Checks.checkEmptyConstructor(subType)) {
                contextCommands.add((ContextCommand<?>) ClassUtils.getInstance(subType));
            } else {
                DIH4JDALogger.error("Could not initialize %s! The class MUST contain a empty public constructor.", subType.getName());
            }
        }
    }

    /**
     * Gets all Commands that were found in {@link InteractionHandler#findSlashCommands(String)} and adds
     * them to the {@link InteractionHandler#slashCommandIndex}.
     */
    @Nonnull
    private Set<SlashCommand> getSlashCommands() {
        Set<SlashCommand> commands = new HashSet<>();
        for (SlashCommand command : this.slashCommands) {
            if (command != null) {
                SlashCommandData data = getBaseCommandData(command);
                command.setCommandData(data);
                if (command.getRegistrationType() != RegistrationType.GUILD && command.getQueueableGuilds().length != 0) {
                    throw new UnsupportedOperationException(command.getClass().getName() + " attempted to require guilds for a non-global command!");
                }
                searchForAutoCompletable(command, command.getClass());
                commands.add(command);
            }
        }
        return commands;
    }

    /**
     * Searches for {@link SlashCommand}s or {@link SlashCommand.Subcommand}s which implement the {@link AutoCompletable} interface.
     *
     * @param command The base {@link SlashCommand}.
     * @param clazz   The command's class.
     */
    private void searchForAutoCompletable(@Nonnull SlashCommand command, @Nonnull Class<? extends SlashCommand> clazz) {
        // check base command
        String baseName = command.getCommandData().getName();
        if (Checks.checkImplementation(clazz, AutoCompletable.class)) {
            autoCompleteIndex.put(baseName, (AutoCompletable) command);
        }
        // check subcommands
        for (SlashCommand.Subcommand child : command.getSubcommands()) {
            if (Checks.checkImplementation(child.getClass(), AutoCompletable.class)) {
                autoCompleteIndex.put(CommandUtils.buildCommandPath(baseName, child.getCommandData().getName()), (AutoCompletable) child);
            }
        }
        // check subcommand groups
        for (SlashCommand.SubcommandGroup childGroup : command.getSubcommandGroups()) {
            String groupName = childGroup.getData().getName();
            // check subcommands
            for (SlashCommand.Subcommand child : childGroup.getSubcommands()) {
                if (Checks.checkImplementation(child.getClass(), AutoCompletable.class)) {
                    autoCompleteIndex.put(CommandUtils.buildCommandPath(baseName, groupName, child.getCommandData().getName()), (AutoCompletable) child);
                }
            }
        }
    }

    /**
     * Gets the complete {@link SlashCommandData} (including Subcommands & Subcommand Groups) from a single {@link SlashCommand}.
     *
     * @param command The base command's instance.
     * @return The new {@link CommandListUpdateAction}.
     */
    @Nonnull
    private SlashCommandData getBaseCommandData(@Nonnull SlashCommand command) {
        // find component (and modal) handlers
        SlashCommandData commandData = command.getCommandData();
        if (command.getSubcommandGroups().length != 0) {
            commandData.addSubcommandGroups(getSubcommandGroupData(command));
        }
        if (command.getSubcommands().length != 0) {
            commandData.addSubcommands(getSubcommandData(command, command.getSubcommands(), null));
        }
        if (command.getSubcommandGroups().length == 0 && command.getSubcommands().length == 0) {
            slashCommandIndex.put(CommandUtils.buildCommandPath(commandData.getName()), command);
            DIH4JDALogger.info(DIH4JDALogger.Type.SLASH_COMMAND_REGISTERED, "\t[*] Registered command: /%s (%s)", command.getCommandData().getName(), command.getRegistrationType().name());
        }
        return commandData;
    }

    /**
     * Gets all {@link SubcommandGroupData} (including Subcommands) from a single {@link SlashCommand}.
     *
     * @param command The base command's instance.
     * @return All {@link SubcommandGroupData} stored in a List.
     */
    @Nonnull
    private Set<SubcommandGroupData> getSubcommandGroupData(@Nonnull SlashCommand command) {
        Set<SubcommandGroupData> groupDataList = new HashSet<>();
        for (SlashCommand.SubcommandGroup group : command.getSubcommandGroups()) {
            if (group != null) {
                SubcommandGroupData groupData = group.getData();
                if (group.getSubcommands().length == 0) {
                    DIH4JDALogger.warn("SubcommandGroup %s is missing Subcommands. It will be ignored.", groupData.getName());
                    continue;
                }
                groupData.addSubcommands(getSubcommandData(command, group.getSubcommands(), groupData.getName()));
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
    @Nonnull
    private Set<SubcommandData> getSubcommandData(@Nonnull SlashCommand command, @Nonnull SlashCommand.Subcommand[] subcommands,
                                                  @Nullable String subGroupName) {
        Set<SubcommandData> subDataList = new HashSet<>();
        for (SlashCommand.Subcommand subcommand : subcommands) {
            if (subcommand != null) {
                String commandPath;
                if (subGroupName == null) {
                    commandPath = CommandUtils.buildCommandPath(command.getCommandData().getName(), subcommand.getCommandData().getName());
                } else {
                    commandPath = CommandUtils.buildCommandPath(command.getCommandData().getName(), subGroupName, subcommand.getCommandData().getName());
                }
                subcommandIndex.put(commandPath, subcommand);
                DIH4JDALogger.info(DIH4JDALogger.Type.SLASH_COMMAND_REGISTERED, "\t[*] Registered command: /%s (%s)", commandPath, command.getRegistrationType().name());
                subDataList.add(subcommand.getCommandData());
            }
        }
        return subDataList;
    }

    /**
     * Gets all Guild Context commands registered in {@link InteractionHandler#findContextCommands(String)} and
     * returns their {@link CommandData} as a List.
     */
    @Nonnull
    private Set<ContextCommand<?>> getContextCommandData() {
        Set<ContextCommand<?>> commands = new HashSet<>();
        for (ContextCommand<?> context : contextCommands) {
            if (context != null) {
                CommandData data = getContextCommandData(context);
                if (data != null) {
                    context.setCommandData(data);
                }
                if (context.getRegistrationType() != RegistrationType.GUILD && context.getQueueableGuilds().length != 0) {
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
     * @param command The base context command's instance.
     * @return The new {@link CommandListUpdateAction}.
     */
    @Nullable
    private CommandData getContextCommandData(@Nonnull ContextCommand<?> command) {
        CommandData data = command.getCommandData();
        if (data.getType() == Command.Type.MESSAGE) {
            messageContextIndex.put(data.getName(), (ContextCommand.Message) command);
        } else if (command.getCommandData().getType() == Command.Type.USER) {
            userContextIndex.put(data.getName(), (ContextCommand.User) command);
        } else {
            DIH4JDALogger.error("Invalid Command Type \"%s\" for Context Command! This command will be ignored.", data.getType());
            return null;
        }
        DIH4JDALogger.info(DIH4JDALogger.Type.CONTEXT_COMMAND_REGISTERED, "\t[*] Registered context command: %s (%s)", data.getName(), command.getRegistrationType().name());
        return data;
    }

    /**
     * Handles a single {@link SlashCommand} or {@link SlashCommand.Subcommand}.
     * If a {@link SlashCommandInteractionEvent} is fired the corresponding class is found and the command is executed.
     *
     * @param event The {@link SlashCommandInteractionEvent} that was fired.
     */
    private void handleSlashCommand(@Nonnull SlashCommandInteractionEvent event) throws CommandNotRegisteredException {
        SlashCommand slashcommand = slashCommandIndex.get(event.getFullCommandName());
        SlashCommand.Subcommand subcommand = subcommandIndex.get(event.getFullCommandName());
        if (slashcommand == null && subcommand == null) {
            if (config.isThrowUnregisteredException()) {
                throw new CommandNotRegisteredException(String.format("Slash Command \"%s\" is not registered.", event.getFullCommandName()));
            }
        } else {
            // check for parent if command is subcommand
            if (slashcommand == null) {
                BaseApplicationCommand<SlashCommandInteractionEvent, ?> base = subcommand.getParent();
                if (base != null) {
                    if (passesRequirements(event, base, base.getRegistrationType()) && passesRequirements(event, subcommand, base.getRegistrationType())) {
                        subcommand.execute(event);
                    }
                }
            } else if (passesRequirements(event, slashcommand, slashcommand.getRegistrationType())) {
                slashcommand.execute(event);
            }
        }
    }

    /**
     * Handles a single {@link ContextCommand.User}.
     * If a {@link UserContextInteractionEvent} is fired the corresponding class is found and the command is executed.
     *
     * @param event The {@link UserContextInteractionEvent} that was fired.
     */
    private void handleUserContextCommand(@Nonnull UserContextInteractionEvent event) throws CommandNotRegisteredException {
        ContextCommand.User context = userContextIndex.get(event.getFullCommandName());
        if (context == null) {
            if (config.isThrowUnregisteredException()) {
                throw new CommandNotRegisteredException(String.format("Context Command \"%s\" is not registered.", event.getFullCommandName()));
            }
        } else {
            if (passesRequirements(event, context, context.getRegistrationType())) {
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
        ContextCommand.Message context = messageContextIndex.get(event.getFullCommandName());
        if (context == null) {
            if (config.isThrowUnregisteredException()) {
                throw new CommandNotRegisteredException(String.format("Context Command \"%s\" is not registered.", event.getFullCommandName()));
            }
        } else {
            if (passesRequirements(event, context, context.getRegistrationType())) {
                context.execute(event);
            }
        }
    }

    /**
     * Checks if the given {@link CommandInteraction} passes the
     * {@link RestrictedCommand} requirements.
     * If not, this will then fire the corresponding event using {@link DIH4JDAEvent#fire(DIH4JDAEvent)}
     *
     * @param interaction The {@link CommandInteraction}.
     * @param command     The {@link RestrictedCommand} which contains the (possible) restrictions.
     * @param type        The {@link RegistrationType} of the {@link BaseApplicationCommand}.
     * @return Whether the event was fired.
     * @since v1.5
     */
    private boolean passesRequirements(@Nonnull CommandInteraction interaction, @Nonnull RestrictedCommand command,
                                       @Nonnull RegistrationType type) {
        long userId = interaction.getUser().getIdLong();
        Long[] guildIds = command.getRequiredGuilds();
        Permission[] permissions = command.getRequiredPermissions();
        Long[] userIds = command.getRequiredUsers();
        Long[] roleIds = command.getRequiredRoles();
        if (type == RegistrationType.GUILD && guildIds.length != 0 && interaction.isFromGuild() &&
                interaction.isFromGuild() && !List.of(guildIds).contains(interaction.getGuild().getIdLong())
        ) {
            DIH4JDAEvent.fire(new InvalidGuildEvent(dih4jda, interaction, Set.of(guildIds)));
            return false;
        }
        if (permissions.length != 0 && interaction.isFromGuild() &&
                interaction.getMember() != null && !interaction.getMember().hasPermission(permissions)) {
            DIH4JDAEvent.fire(new InsufficientPermissionsEvent(dih4jda, interaction, Set.of(permissions)));
            return false;
        }
        if (userIds.length != 0 && !List.of(userIds).contains(userId)) {
            DIH4JDAEvent.fire(new InvalidUserEvent(dih4jda, interaction, Set.of(userIds)));
            return false;
        }
        if (interaction.isFromGuild() && interaction.getMember() != null) {
            Member member = interaction.getMember();
            if (roleIds.length != 0 && !member.getRoles().isEmpty() &&
                    member.getRoles().stream().noneMatch(r -> List.of(roleIds).contains(r.getIdLong()))) {
                DIH4JDAEvent.fire(new InvalidRoleEvent(dih4jda, interaction, Set.of(roleIds)));
                return false;
            }
        }
        // check if the command has enabled some sort of cooldown
        if (!command.getCommandCooldown().equals(Duration.ZERO)) {
            if (command.hasCooldown(userId)) {
                DIH4JDAEvent.fire(new CommandCooldownEvent(dih4jda, interaction, command.retrieveCooldown(userId)));
                return false;
            } else {
                command.applyCooldown(userId, Instant.now().plus(command.getCommandCooldown()));
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
            } catch (Throwable e) {
                DIH4JDAEvent.fire(new CommandExceptionEvent(dih4jda, event, e));
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
            } catch (Throwable e) {
                DIH4JDAEvent.fire(new CommandExceptionEvent(dih4jda, event, e));
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
            } catch (Throwable e) {
                DIH4JDAEvent.fire(new CommandExceptionEvent(dih4jda, event, e));
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
                AutoCompletable autoComplete = autoCompleteIndex.get(event.getFullCommandName());
                if (autoComplete != null) {
                    autoComplete.handleAutoComplete(event, event.getFocusedOption());
                }
            } catch (Throwable e) {
                DIH4JDAEvent.fire(new AutoCompleteExceptionEvent(dih4jda, event, e));
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
                if (dih4jda.getButtonMappings().length == 0) return;
                Optional<ButtonHandler> buttonOptional = Arrays.stream(dih4jda.getButtonMappings())
                        .filter(map -> List.of(map.getIds()).contains(ComponentIdBuilder.split(event.getComponentId())[0]))
                        .map(IdMapping::getHandler)
                        .findFirst();
                if (buttonOptional.isEmpty()) {
                    DIH4JDALogger.warn(DIH4JDALogger.Type.BUTTON_NOT_FOUND, "Button with id \"%s\" could not be found.", event.getComponentId());
                } else {
                    buttonOptional.get().handleButton(event, event.getButton());
                }
            } catch (Throwable e) {
                DIH4JDAEvent.fire(new ComponentExceptionEvent(dih4jda, event, e));
            }
        }, config.getExecutor());
    }

    /**
     * Fired if Discord reports a {@link StringSelectInteractionEvent}.
     *
     * @param event The {@link StringSelectInteractionEvent} that was fired.
     */
    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                if (dih4jda.getStringSelectMenuMappings().length == 0) return;
                Optional<StringSelectMenuHandler> selectMenuOptional = Arrays.stream(dih4jda.getStringSelectMenuMappings())
                        .filter(map -> List.of(map.getIds()).contains(ComponentIdBuilder.split(event.getComponentId())[0]))
                        .map(IdMapping::getHandler)
                        .findFirst();
                if (selectMenuOptional.isEmpty()) {
                    DIH4JDALogger.warn(DIH4JDALogger.Type.SELECT_MENU_NOT_FOUND, "Select Menu with id \"%s\" could not be found.", event.getComponentId());
                } else {
                    selectMenuOptional.get().handleStringSelectMenu(event, event.getValues());
                }
            } catch (Throwable e) {
                DIH4JDAEvent.fire(new ComponentExceptionEvent(dih4jda, event, e));
            }
        }, config.getExecutor());
    }

    @Override
    public void onEntitySelectInteraction(@Nonnull EntitySelectInteractionEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                if (dih4jda.getEntitySelectMenuMappings().length == 0) return;
                Optional<EntitySelectMenuHandler> selectMenuOptional = Arrays.stream(dih4jda.getEntitySelectMenuMappings())
                        .filter(map -> List.of(map.getIds()).contains(ComponentIdBuilder.split(event.getComponentId())[0]))
                        .map(IdMapping::getHandler)
                        .findFirst();
                if (selectMenuOptional.isEmpty()) {
                    DIH4JDALogger.warn(DIH4JDALogger.Type.SELECT_MENU_NOT_FOUND, "Select Menu with id \"%s\" could not be found.", event.getComponentId());
                } else {
                    selectMenuOptional.get().handleEntitySelectMenu(event, event.getValues());
                }
            } catch (Throwable e) {
                DIH4JDAEvent.fire(new ComponentExceptionEvent(dih4jda, event, e));
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
                if (dih4jda.getModalMappings().length == 0) return;
                Optional<ModalHandler> modalOptional = Arrays.stream(dih4jda.getModalMappings())
                        .filter(map -> List.of(map.getIds()).contains(ComponentIdBuilder.split(event.getModalId())[0]))
                        .map(IdMapping::getHandler)
                        .findFirst();
                if (modalOptional.isEmpty()) {
                    DIH4JDALogger.warn(DIH4JDALogger.Type.MODAL_NOT_FOUND, "Modal with id \"%s\" could not be found.", event.getModalId());
                } else {
                    modalOptional.get().handleModal(event, event.getValues());
                }
            } catch (Throwable e) {
                DIH4JDAEvent.fire(new ModalExceptionEvent(dih4jda, event, e));
            }
        }, config.getExecutor());
    }
}
