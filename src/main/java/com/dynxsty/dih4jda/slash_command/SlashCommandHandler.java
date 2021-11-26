package com.dynxsty.dih4jda.slash_command;

import com.dynxsty.dih4jda.slash_command.dto.SlashCommand;
import com.dynxsty.dih4jda.slash_command.dto.SlashSubCommand;
import com.dynxsty.dih4jda.slash_command.dto.SlashSubCommandGroup;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.reflections.Reflections.log;

public class SlashCommandHandler extends ListenerAdapter {

    private final String commandsPackage;
    private HashMap<String, SlashCommandInteraction> slashCommands;

    /**
     * Constructs a new {@link SlashCommandHandler} from the supplied commands package.
     * @param commandsPackage The package that houses the command classes.
     */
    public SlashCommandHandler(String commandsPackage) {
        this.commandsPackage = commandsPackage;
    }

    /**
     * Registers all slash commands. Loops through all classes found in the commands package that is a subclass of {@link SlashCommand}.
     * Goes through these steps with every iteration;
     * <ol>
     *     <li>Checks if the class is missing {@link CommandData} and doesn't register if it is.</li>
     *     <li>Checks if the class is neither a subclass of {@link SlashSubCommand} nor {@link SlashSubCommandGroup} and registers it as regular command.</li>
     *     <li>Checks if the class is a subclass of {@link SlashSubCommandGroup} if it is, the SlashCommandGroup is validated and another loop is fired following the two steps above for the group's sub commands.</li>
     *     <li>Checks if the class is a subclass of {@link SlashSubCommand}, if it is, it is registered as a sub command.</li>
     * </ol>
     * @param updateAction The {@link CommandListUpdateAction} that is used to register the commands.
     * @throws Exception if anything goes wrong.
     */
    public void registerSlashCommands(CommandListUpdateAction updateAction) throws Exception {
        this.slashCommands = new HashMap<>();

        Reflections commands = new Reflections(this.commandsPackage);
        Set<Class<? extends SlashCommand>> classes = commands.getSubTypesOf(SlashCommand.class);
        for (var clazz : classes) {

            CommandData cmdData;
            String first, second = null, third = null;
            CommandPrivilege[] privileges;

            SlashCommand instance = clazz.getConstructor().newInstance();

            if (instance.getCommandData() == null) {
                log.warn("Class {} is missing CommandData. It will be ignored.", clazz.getName());
                continue;
            }

            cmdData = instance.getCommandData();
            first = instance.getCommandData().getName();
            privileges = instance.getCommandPrivileges();

            if (instance.getSubCommandClasses() == null && instance.getSubCommandGroupClasses() == null) {
                slashCommands.put(getFullCommandName(first, second, third), new SlashCommandInteraction((ISlashCommand) instance, privileges));
            }

            log.info("[*] Added CommandData from Class {}", clazz.getSimpleName());

            if (instance.getSubCommandGroupClasses() != null) {
                for (var subGroupClazz : instance.getSubCommandGroupClasses()) {
                    SlashSubCommandGroup subGroupInstance = subGroupClazz.getDeclaredConstructor().newInstance();
                    second = subGroupInstance.getSubCommandGroupData().getName();

                    if (subGroupInstance.getSubCommandGroupData() == null) {
                        log.warn("Class {} is missing SubCommandGroupData. It will be ignored.", subGroupClazz.getName());
                        continue;
                    }
                    log.info("\t[{}] Adding SubCommandGroupData from Class {}",
                            clazz.getSimpleName(), subGroupClazz.getSimpleName());

                    if (subGroupInstance.getSubCommandClasses() == null) {
                        log.warn("Class {} is missing SubCommandClasses. It will be ignored.", subGroupClazz.getName());
                        continue;
                    }

                    SubcommandGroupData subCmdGroupData = subGroupInstance.getSubCommandGroupData();
                    for (var subClazz : subGroupInstance.getSubCommandClasses()) {
                        SlashSubCommand subInstance = subClazz.getDeclaredConstructor().newInstance();
                        third = subInstance.getSubCommandData().getName();
                        if (subInstance.getSubCommandData() == null) {
                            log.warn("Class {} is missing SubCommandData. It will be ignored.", subClazz.getName());
                            continue;
                        }
                        slashCommands.put(getFullCommandName(first, second, third), new SlashCommandInteraction((ISlashCommand) subInstance, privileges));
                        subCmdGroupData.addSubcommands(subInstance.getSubCommandData());

                        log.info("\t\t[{}] Added SubCommandData from Class {}",
                                subGroupClazz.getSimpleName(), subClazz.getSimpleName());
                    }
                    cmdData.addSubcommandGroups(subCmdGroupData);
                }
            }

            if (instance.getSubCommandClasses() != null) {
                for (var subClazz : instance.getSubCommandClasses()) {
                    SlashSubCommand subInstance = subClazz.getDeclaredConstructor().newInstance();
                    second = null;
                    third = subInstance.getSubCommandData().getName();
                    if (subInstance.getSubCommandData() == null) {
                        log.warn("Class {} is missing SubCommandData. It will be ignored.", subClazz.getName());
                    }
                    slashCommands.put(getFullCommandName(first, second, third), new SlashCommandInteraction((ISlashCommand) subInstance, privileges));
                    cmdData.addSubcommands(subInstance.getSubCommandData());

                    log.info("\t[{}] Added SubCommandData from Class {}",
                            clazz.getSimpleName(), subClazz.getSimpleName());
                }
            }
            updateAction.addCommands(cmdData);
        }
        log.info("[*] Queuing SlashCommands");
        updateAction.queue();
    }

    /**
     * If a {@link SlashCommandEvent} is fired the corresponding class is found and the command is executed.
     * @param event The {@link SlashCommandEvent} that was fired.
     */
    private void handleCommand(SlashCommandEvent event) {
        event.deferReply().queue();
        try {
            var command = slashCommands.get(getFullCommandName(event.getName(), event.getSubcommandGroup(), event.getSubcommandName()));
            command.handler().handleSlash(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to create one command name out of the SlashCommand, SlashSubCommandGroup and SlashSubCommand
     * @param first The SlashCommand's name.
     * @param second The SlashSubCommandGroup's name.
     * @param third The SlashSubCommand's name.
     * @return One combined string.
     */
    private String getFullCommandName(String first, String second, String third) {
        return first + " " + second + " " + third;
    }

    /**
     * Fired if Discord reports a {@link SlashCommandEvent}.
     * @param event The {@link SlashCommandEvent} that was fired.
     */
    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        CompletableFuture.runAsync(() -> handleCommand(event));
    }
}
