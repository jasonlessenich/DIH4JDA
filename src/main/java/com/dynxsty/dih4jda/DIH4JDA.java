package com.dynxsty.dih4jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

//TODO-v1.4: Documentation
public class DIH4JDA extends ListenerAdapter {

    private final JDA jda;
    private final String commandsPackage;
    private final long ownerId;
    private final Set<DIH4JDALogger.Type> blockedLogTypes;
    private final boolean registerOnStartup;
    private final boolean smartQueuing;

    private InteractionHandler handler;

    /**
     * Constructs a new DIH4JDA instance
     * @param jda The {@link JDA} instance the handler is to be used for.
     * @param commandsPackage The package that houses the command classes.
     * @param ownerId The ID of the owner - used for admin-only commands.
     * @param blockedLogTypes All Logs that should be blocked.
     */
    protected DIH4JDA(JDA jda, String commandsPackage, long ownerId, boolean registerOnStartup, boolean smartQueuing, DIH4JDALogger.Type... blockedLogTypes) {
        this.jda = jda;
        this.ownerId = ownerId;
        this.commandsPackage = commandsPackage;
        this.registerOnStartup = registerOnStartup;
        this.smartQueuing = smartQueuing;
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
        if (this.getCommandsPackage() == null) return;
        DIH4JDALogger.blockedLogTypes = blockedLogTypes;
        this.handler = new InteractionHandler(this);
        this.getJDA().addEventListener(handler);
        try {
            if (this.registerOnStartup) this.handler.registerInteractions(this.jda);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Registers all Interactions and replaces the old ones.
     * Please note that global commands may need up to an hour before they're fully registered.
     */
    public void registerInteractions() throws Exception {
        this.handler.registerInteractions(this.jda);
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

    public Set<DIH4JDALogger.Type> getBlockedLogTypes() {
        return this.blockedLogTypes;
    }

    public boolean isRegisterOnStartup() {
        return this.registerOnStartup;
    }

    public boolean isSmartQueuing() {
        return this.smartQueuing;
    }
}
