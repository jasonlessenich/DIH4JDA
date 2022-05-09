package com.dynxsty.dih4jda;

import com.dynxsty.dih4jda.events.DIH4JDAListenerAdapter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

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

	public static boolean defaultGuildCommands = true;
	private final JDA jda;
	private final String commandsPackage;
	private final Set<DIH4JDALogger.Type> blockedLogTypes;
	private final boolean registerOnStartup;
	private final boolean smartQueuing;
	private final Set<Object> listeners;
	private InteractionHandler handler;

	/**
	 * Constructs a new DIH4JDA instance
	 *
	 * @param jda                The {@link JDA} instance the handler is to be used for.
	 * @param commandsPackage The package that houses the command classes.
	 * @param blockedLogTypes    All Logs that should be blocked.
	 */
	protected DIH4JDA(JDA jda, String commandsPackage, boolean registerOnStartup, boolean smartQueuing, DIH4JDALogger.Type... blockedLogTypes) {
		this.jda = jda;
		this.commandsPackage = commandsPackage;
		this.registerOnStartup = registerOnStartup;
		this.smartQueuing = smartQueuing;
		if (blockedLogTypes == null || blockedLogTypes.length < 1) {
			this.blockedLogTypes = new HashSet<>();
		} else {
			this.blockedLogTypes = Arrays.stream(blockedLogTypes).collect(Collectors.toSet());
		}
		this.listeners = new HashSet<>();
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
			if (registerOnStartup) handler.registerInteractions();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Registers all Interactions and replaces the old ones.
	 * Please note that global commands may need up to an hour before they're fully registered.
	 */
	public void registerInteractions() throws ReflectiveOperationException {
		handler.registerInteractions();
	}

	/**
	 * @return The {@link JDA} instance.
	 */
	public JDA getJDA() {
		return jda;
	}

	/**
	 * @return The provided package that is used with the {@link Reflections} API.
	 */
	public String getCommandsPackage() {
		return commandsPackage;
	}

	/**
	 * @return A set with all blocked {@link DIH4JDALogger.Type}s.
	 */
	public Set<DIH4JDALogger.Type> getBlockedLogTypes() {
		return blockedLogTypes;
	}

	/**
	 * @return Whether commands should be registered on each {@link ListenerAdapter#onReady(ReadyEvent)} event.
	 */
	public boolean isRegisterOnStartup() {
		return registerOnStartup;
	}

	/**
	 * @return Whether the SmartQueue Functionality is enabled.
	 */
	public boolean isSmartQueuing() {
		return smartQueuing;
	}

	/**
	 * Allows to add Listener classes (that extend {@link DIH4JDAListenerAdapter}).
	 * @since v1.5
	 */
	public void addListeners(Object... classes) {
		for (Object o : classes) {
			try {
				// check if class extends the ListenerAdapter
				DIH4JDAListenerAdapter adapter = (DIH4JDAListenerAdapter) o;
			} catch (ClassCastException e) {
				throw new IllegalArgumentException("Listener classes must extend DIH4JDAListenerAdapter!");
			}
		}
		listeners.add(Arrays.asList(classes));
	}

	/**
	 * @return A set of all Listener classes.
	 * @see DIH4JDA#addListeners(Object...)
	 */
	protected Set<Object> getListeners() {
		return listeners;
	}
}
