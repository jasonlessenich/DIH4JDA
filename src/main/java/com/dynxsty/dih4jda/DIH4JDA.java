package com.dynxsty.dih4jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The entry-point of this Handler.
 *
 * <h2>Creating a new DIH4JDA instance</h2>
 * <pre>{@code
 * DIH4JDA dih4JDA = DIH4JDABuilder
 *         .setJDA(jda) // Your JDA instance
 *         .setCommandsPackage("com.dynxsty.superawesomebot.commands") // The main package where all your commands are in.
 *         .build();
 * }</pre>
 * Upon calling .build();, the bot will automatically register all Commands that are in the given commandsPackage.
 * (if not disabled)
 */
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
        if (getCommandsPackage() == null) return;
        DIH4JDALogger.blockedLogTypes = blockedLogTypes;
        handler = new InteractionHandler(this);
        getJDA().addEventListener(handler);
        try {
            if (registerOnStartup) handler.registerInteractions(jda);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Registers all Interactions and replaces the old ones.
     * Please note that global commands may need up to an hour before they're fully registered.
     */
    public void registerInteractions() throws Exception {
        handler.registerInteractions(jda);
    }

    public JDA getJDA() {
        return jda;
    }

    public String getCommandsPackage() {
        return commandsPackage;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public Set<DIH4JDALogger.Type> getBlockedLogTypes() {
        return blockedLogTypes;
    }

    public boolean isRegisterOnStartup() {
        return registerOnStartup;
    }

    public boolean isSmartQueuing() {
        return smartQueuing;
    }
}
