package com.dynxsty.dih4jda;

import com.dynxsty.dih4jda.slash_command.SlashCommandHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.CompletableFuture;

public class DIH4JDA extends ListenerAdapter {

    public static JDA jda;
    public static SlashCommandType commandType;
    public static String ownerId, commandsPackage;

    DIH4JDA(JDA jda, SlashCommandType commandType, String commandsPackage, String ownerId) {
        DIH4JDA.jda = jda;
        DIH4JDA.commandType = commandType;
        DIH4JDA.ownerId = ownerId;
        DIH4JDA.commandsPackage = commandsPackage;
        jda.addEventListener(this);
    }

    @Override
    public void onReady(ReadyEvent event) {
        if (commandsPackage == null) return;

        SlashCommandHandler handler = new SlashCommandHandler(commandsPackage);
        jda.addEventListener(handler);

        CompletableFuture.runAsync(() -> {
            try {
                switch (commandType) {
                    case GUILD -> { for (var g : jda.getGuilds()) { handler.registerSlashCommands(g.updateCommands()); } }
                    case GLOBAL -> handler.registerSlashCommands(jda.updateCommands());
                    default -> throw new IllegalStateException("Invalid commandType: " + commandType);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
}
