package com.dynxsty.dih4jda.slash_command;

import com.dynxsty.dih4jda.slash_command.dto.SlashCommand;
import com.dynxsty.dih4jda.slash_command.dto.SlashSubCommand;
import com.dynxsty.dih4jda.slash_command.dto.SlashSubCommandGroup;
import net.dv8tion.jda.api.entities.Guild;
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

    private final HashMap<String, SlashCommandInteraction> slashCommands;
    private final String commandsPackage;

    public SlashCommandHandler(String commandsPackage) {
        this.slashCommands = new HashMap<>();
        this.commandsPackage = commandsPackage;
    }

    public void registerSlashCommands(Guild guild) throws Exception {
        CommandListUpdateAction updateAction = guild.updateCommands();

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

    private void handleCommand(SlashCommandEvent event) {
        event.deferReply().queue();
        try {
            var command = slashCommands.get(getFullCommandName(event.getName(), event.getSubcommandGroup(), event.getSubcommandName()));
            command.handler().handleSlash(event).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFullCommandName(String first, String second, String third) {
        return first + " " + second + " " + third;
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        CompletableFuture.runAsync(() -> handleCommand(event));
    }
}
