package com.dynxsty.dih4jda;

import com.dynxsty.dih4jda.events.DIH4JDAListenerAdapter;
import com.dynxsty.dih4jda.interactions.commands.SlashCommand;
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

	private final JDA jda;
	private final String commandsPackage;
	private final long ownerId;
	private final Set<DIH4JDALogger.Type> blockedLogTypes;
	private final boolean registerOnStartup;
	private final boolean smartQueuing;

	private final Set<Class<? extends DIH4JDAListenerAdapter>> listeners;

	private InteractionHandler handler;

	/**
	 * Constructs a new DIH4JDA instance
	 *
	 * @param jda             The {@link JDA} instance the handler is to be used for.
	 * @param commandsPackage The package that houses the command classes.
	 * @param ownerId         The ID of the owner - used for admin-only commands.
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
		this.listeners = new HashSet<>();
		findListenerClasses();
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
	public void registerInteractions() throws Exception {
		handler.registerInteractions();
	}

	// TODO v1.5: Documentation
	public JDA getJDA() {
		return jda;
	}

	// TODO v1.5: Documentation
	public String getCommandsPackage() {
		return commandsPackage;
	}

	// TODO v1.5: Documentation
	public long getOwnerId() {
		return ownerId;
	}

	// TODO v1.5: Documentation
	public Set<DIH4JDALogger.Type> getBlockedLogTypes() {
		return blockedLogTypes;
	}

	// TODO v1.5: Documentation
	public boolean isRegisterOnStartup() {
		return registerOnStartup;
	}

	// TODO v1.5: Documentation
	public boolean isSmartQueuing() {
		return smartQueuing;
	}

	// TODO v1.5: Documentation
	private void findListenerClasses() {
		Reflections classes = new Reflections(commandsPackage);
		listeners.addAll(classes.getSubTypesOf(DIH4JDAListenerAdapter.class));
	}

	// TODO v1.5: Documentation
	protected Set<Class<? extends DIH4JDAListenerAdapter>> getListeners() {
		return listeners;
	}
}
