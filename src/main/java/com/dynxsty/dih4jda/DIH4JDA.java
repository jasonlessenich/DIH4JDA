package com.dynxsty.dih4jda;

import com.dynxsty.dih4jda.config.DIH4JDAConfig;
import com.dynxsty.dih4jda.events.DIH4JDAListenerAdapter;
import com.dynxsty.dih4jda.interactions.commands.ExecutableCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.Set;
import java.util.concurrent.Executor;

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

    public static ExecutableCommand.Type defaultCommandType;
    private final DIH4JDAConfig config;
    private Set<DIH4JDAListenerAdapter> listeners;
    private InteractionHandler handler;

    /**
     * Constructs a new DIH4JDA instance
     *
     * @param config The instance's configuration.
     */
    protected DIH4JDA(DIH4JDAConfig config) {
        if (defaultCommandType == null) defaultCommandType = ExecutableCommand.Type.GUILD;
        this.config = config;
        try {
            this.handler = new InteractionHandler(this);
            config.getJDA().addEventListener(this, handler);
        } catch (ReflectiveOperationException e) {
            DIH4JDALogger.warn("Could not initialize Interaction Handler: " + e.getMessage());
        }
    }

    /**
     * Ran once the {@link JDA} instance fires the {@link ReadyEvent}.
     *
     * @param event The {@link ReadyEvent} that was fired.
     */
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (config.getCommandsPackage() == null) return;
        DIH4JDALogger.blockedLogTypes = config.getBlockedLogTypes();
        try {
            if (config.isRegisterOnReady() && handler != null) {
                handler.registerInteractions();
            }
        } catch (ReflectiveOperationException e) {
            DIH4JDALogger.error("Could not register commands: " + e.getMessage());
        }
    }

    /**
     * Registers all Interactions and replaces the old ones.
     * Please note that global commands may need up to an hour before they're fully registered.
     */
    public void registerInteractions() throws ReflectiveOperationException {
        if (handler != null) {
            handler.registerInteractions();
        }
    }

    /**
     * @return The instance's configuration.
     */
    public DIH4JDAConfig getConfig() {
        return config;
    }

    /**
     * Allows to add Listener classes (that extend {@link DIH4JDAListenerAdapter}).
     *
     * @since v1.5
     */
    public void addListener(Object... classes) {
        for (Object o : classes) {
            try {
                // check if class extends the ListenerAdapter
                DIH4JDAListenerAdapter adapter = (DIH4JDAListenerAdapter) o;
                listeners.add(adapter);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Listener classes must extend DIH4JDAListenerAdapter!");
            }
        }
    }

    /**
     * @return A set of all Listener classes.
     * @see DIH4JDA#addListener(Object...)
     */
    protected Set<DIH4JDAListenerAdapter> getListeners() {
        return listeners;
    }
}
