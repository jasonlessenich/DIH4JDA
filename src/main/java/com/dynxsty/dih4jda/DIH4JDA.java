package com.dynxsty.dih4jda;

import com.dynxsty.dih4jda.commands.InteractionHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class DIH4JDA extends ListenerAdapter {

    public JDA jda;
    public String commandsPackage;
    public long ownerId;

    public static final org.slf4j.Logger log = JDALogger.getLog(DIH4JDA.class);

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
     * Ran once the {@link JDA} instance fires the {@link ReadyEvent}.
     *
     * @param event The {@link ReadyEvent} that was fired.
     */
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (commandsPackage == null) return;
        InteractionHandler handler = new InteractionHandler(commandsPackage);
        this.jda.addEventListener(handler);
        CompletableFuture.runAsync(() -> {
            try {
                handler.registerSlashCommands(this.jda);
                handler.registerContextCommands(this.jda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
