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

    /**
     * Constructs a new DIH4JDA instance
     * @param jda The {@link JDA} instance the handler is to be used for.
     * @param commandType The {@link SlashCommandType} the handler should use.
     * @param commandsPackage The package that houses the command classes.
     * @param ownerId The ID of the owner - used for admin-only commands.
     */
    DIH4JDA(JDA jda, SlashCommandType commandType, String commandsPackage, String ownerId) {
        DIH4JDA.jda = jda;
        DIH4JDA.commandType = commandType;
        DIH4JDA.ownerId = ownerId;
        DIH4JDA.commandsPackage = commandsPackage;
        jda.addEventListener(this);
    }

    /**
     * Ran once the {@link JDA} instance fires the {@link ReadyEvent}. Mainly does the following two things;
     * <ol>
     *     <li>Creates a new {@link SlashCommandHandler} instance</li>
     *     <li>Register the Slash commands, depending on the {@link SlashCommandType}, either by looping through all guilds or by registering global slash commands with the JDA instance.</li>
     * </ol>
     * @param event The {@link ReadyEvent} that was fired.
     */
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
