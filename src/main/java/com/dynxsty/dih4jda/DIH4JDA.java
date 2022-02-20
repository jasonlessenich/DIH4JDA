package com.dynxsty.dih4jda;

import com.dynxsty.dih4jda.commands.SlashCommandHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class DIH4JDA extends ListenerAdapter {

    public JDA jda;
    public String commandsPackage;
    public long ownerId;

    /**
     * Constructs a new DIH4JDA instance
     * @param jda The {@link JDA} instance the handler is to be used for.
     * @param commandsPackage The package that houses the command classes.
     * @param ownerId The ID of the owner - used for admin-only commands.
     */
    protected DIH4JDA(JDA jda, String commandsPackage, long ownerId) {
        this.jda = jda;
        this.ownerId = ownerId;
        this.commandsPackage = commandsPackage;
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
    public void onReady(@NotNull ReadyEvent event) {
        if (commandsPackage == null) return;
        SlashCommandHandler handler = new SlashCommandHandler(commandsPackage);
        this.jda.addEventListener(handler);
        CompletableFuture.runAsync(() -> {
            try {
                handler.registerSlashCommands(this.jda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
