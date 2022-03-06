package com.dynxsty.dih4jda;

import com.dynxsty.dih4jda.commands.InteractionHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DIH4JDA extends ListenerAdapter {

    private final JDA jda;
    private final String commandsPackage;
    private final long ownerId;
    private final Set<DIH4JDALogger.Type> blockedLogTypes;

    /**
     * Constructs a new DIH4JDA instance
     * @param jda The {@link JDA} instance the handler is to be used for.
     * @param commandsPackage The package that houses the command classes.
     * @param ownerId The ID of the owner - used for admin-only commands.
     * @param blockedLogTypes All Logs that should be blocked.
     */
    protected DIH4JDA(JDA jda, String commandsPackage, long ownerId, DIH4JDALogger.Type... blockedLogTypes) {
        this.jda = jda;
        this.ownerId = ownerId;
        this.commandsPackage = commandsPackage;
        if (blockedLogTypes == null || blockedLogTypes.length < 1) {
           this.blockedLogTypes = new HashSet<>();
        } else {
            this.blockedLogTypes = Arrays.stream(blockedLogTypes).collect(Collectors.toSet());
        }
        jda.addEventListener(this);
    }

    /**
     * Ran once the {@link JDA} instance fires the {@link ReadyEvent}.
     *
     * @param event The {@link ReadyEvent} that was fired.
     */
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (getCommandsPackage() == null) return;
        InteractionHandler handler = new InteractionHandler(this);
        this.getJDA().addEventListener(handler);
        DIH4JDALogger.blockedLogTypes = blockedLogTypes;
        try {
            handler.registerInteractions(this.jda);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JDA getJDA() {
        return this.jda;
    }

    public String getCommandsPackage() {
        return this.commandsPackage;
    }

    public long getOwnerId() {
        return this.ownerId;
    }
}
