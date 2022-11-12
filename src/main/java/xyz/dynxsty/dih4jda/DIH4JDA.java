package xyz.dynxsty.dih4jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.dynxsty.dih4jda.config.DIH4JDAConfig;
import xyz.dynxsty.dih4jda.events.DIH4JDAEventListener;
import xyz.dynxsty.dih4jda.exceptions.DIH4JDAException;
import xyz.dynxsty.dih4jda.interactions.commands.application.ApplicationCommand;
import xyz.dynxsty.dih4jda.interactions.commands.application.ContextCommand;
import xyz.dynxsty.dih4jda.interactions.commands.application.RegistrationType;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;
import xyz.dynxsty.dih4jda.interactions.components.ButtonHandler;
import xyz.dynxsty.dih4jda.interactions.components.EntitySelectMenuHandler;
import xyz.dynxsty.dih4jda.interactions.components.ModalHandler;
import xyz.dynxsty.dih4jda.interactions.components.StringSelectMenuHandler;
import xyz.dynxsty.dih4jda.util.ComponentIdBuilder;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <h1>DIH4JDA</h1>
 * <h2>Creating a new {@link DIH4JDA} instance:</h2>
 * <pre>{@code
 * DIH4JDA dih4JDA = DIH4JDABuilder
 *         .setJDA(jda) // Your JDA instance
 *         .setCommandsPackages("com.dynxsty.superawesomebot.commands") // The main package where all your commands are in.
 *         .build();
 * }</pre>
 * Upon calling .build();, the bot will automatically register all Commands that are in the given commandsPackage.
 * (if not disabled)
 */
public class DIH4JDA extends ListenerAdapter {

	/**
	 * The default {@link RegistrationType} which is used for queuing new commands.
	 * This can be overridden using {@link ApplicationCommand#setRegistrationType(RegistrationType)}
	 */
	public static RegistrationType defaultCommandType;

	// Component Handler
	private static final Map<List<String>, ButtonHandler> buttonHandlers;
	private static final Map<List<String>, StringSelectMenuHandler> stringSelectMenuHandlers;
	private static final Map<List<String>, EntitySelectMenuHandler> entitySelectMenuHandlers;
	private static final Map<List<String>, ModalHandler> modalHandlers;

	static {
		buttonHandlers = new HashMap<>();
		stringSelectMenuHandlers = new HashMap<>();
		entitySelectMenuHandlers = new HashMap<>();
		modalHandlers = new HashMap<>();
	}

	private final DIH4JDAConfig config;
	private final Set<DIH4JDAEventListener> listeners;
	private final InteractionHandler handler;

	/**
	 * Constructs a new DIH4JDA instance
	 *
	 * @param config The instance's configuration.
	 */
	protected DIH4JDA(DIH4JDAConfig config) throws DIH4JDAException {
		if (defaultCommandType == null) defaultCommandType = RegistrationType.GUILD;
		this.config = config;
		listeners = new HashSet<>();
		DIH4JDALogger.blockedLogTypes = config.getBlockedLogTypes();
		this.handler = new InteractionHandler(this);
		config.getJDA().addEventListener(this, handler);
	}

	/**
	 * Ran once the {@link JDA} instance fires the {@link ReadyEvent}.
	 *
	 * @param event The {@link ReadyEvent} that was fired.
	 */
	@Override
	public void onReady(@Nonnull ReadyEvent event) {
		if (config.getCommandPackages() == null) return;
		try {
			if (config.isRegisterOnReady() && handler != null) {
				handler.registerInteractions();
			}
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
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
	public @Nonnull DIH4JDAConfig getConfig() {
		return config;
	}

	/**
	 * Allows to add Listener classes (that implements {@link DIH4JDAEventListener}).
	 *
	 * @since v1.5
	 */
	public void addEventListener(@Nonnull Object... classes) {
		for (Object o : classes) {
			try {
				// check if class extends the ListenerAdapter
				DIH4JDAEventListener adapter = (DIH4JDAEventListener) o;
				listeners.add(adapter);
			} catch (ClassCastException e) {
				throw new IllegalArgumentException("Listener classes must implement DIH4JDAEventListener!");
			}
		}
	}

	/**
	 * @return A set of all Listener classes.
	 * @see DIH4JDA#addEventListener(Object...)
	 */
	@Nonnull
	public Set<DIH4JDAEventListener> getEventListeners() {
		return listeners;
	}

	/**
	 * Convenience method which replaces {@link DIH4JDA#getConfig()#getJDA()}
	 *
	 * @return The {@link JDA} instance.
	 */
	public @Nonnull JDA getJDA() {
		return config.getJDA();
	}

	/**
	 * Manually registers {@link SlashCommand}s.
	 *
	 * @param commands An array of commands to register.
	 */
	public void addSlashCommands(SlashCommand... commands) {
		handler.slashCommands.addAll(List.of(commands));
	}

	/**
	 * Manually registers {@link ContextCommand}s.
	 *
	 * @param commands An array of commands to register.
	 */
	public void addContextCommands(ContextCommand<?>... commands) {
		handler.contextCommands.addAll(List.of(commands));
	}

	/**
	 * Binds all {@link ButtonHandler}s to their id.
	 * <br>
	 * <pre>{@code
	 * dih4jda.addButtonHandlers(Map.of(
	 * 	List.of("apple"), new AppleButtonHandler(),
	 * 	List.of("banana"), new BananaButtonHandler()
	 * ));
	 * }</pre>
	 * <br>
	 * This is best used in combination with {@link ComponentIdBuilder#build(String, Object...)}.
	 *
	 * @param handlers All {@link ButtonHandler}, as a immutable {@link Map}.
	 */
	public void addButtonHandlers(Map<List<String>, ButtonHandler> handlers) {
		buttonHandlers.putAll(handlers);
	}

	/**
	 * Gets all {@link ButtonHandler}s.
	 *
	 * @return An immutable {@link Map} which stores the id as the <strong>Key</strong> and
	 * the {@link ButtonHandler} as the <strong>Value</strong>.
	 */
	public @Nonnull Map<List<String>, ButtonHandler> getButtonHandlers() {
		return buttonHandlers;
	}

	/**
	 * Binds all {@link StringSelectMenuHandler}s to their id.
	 * <br>
	 * <pre>{@code
	 * dih4jda.addStringSelectMenuHandlers(Map.of(
	 * 	List.of("apple"), new AppleSelectMenuHandler(),
	 * 	List.of("banana"), new BananaSelectMenuHandler()
	 * ));
	 * }</pre>
	 * <br>
	 * This is best used in combination with {@link ComponentIdBuilder#build(String, Object...)}.
	 *
	 * @see DIH4JDA#addEntitySelectMenuHandlers(Map)
	 * @param handlers All {@link StringSelectMenuHandler}, as a immutable {@link Map}.
	 */
	public void addStringSelectMenuHandlers(Map<List<String>, StringSelectMenuHandler> handlers) {
		stringSelectMenuHandlers.putAll(handlers);
	}

	/**
	 * Binds all {@link EntitySelectMenuHandler}s to their id,
	 * <br>
	 * <pre>{@code
	 * dih4jda.addEntitySelectMenuHandlers(Map.of(
	 * 	List.of("apple"), new AppleSelectMenuHandler(),
	 * 	List.of("banana"), new BananaSelectMenuHandler()
	 * ));
	 * }</pre>
	 * <br>
	 *  * This is best used in combination with {@link ComponentIdBuilder#build(String, Object...)}.
	 *
	 * @see DIH4JDA#addStringSelectMenuHandlers(Map)
	 * @param handlers All {@link EntitySelectMenuHandler}, as a immutable {@link Map}.
	 */
	public void addEntitySelectMenuHandlers(Map<List<String>, EntitySelectMenuHandler> handlers) {
		entitySelectMenuHandlers.putAll(handlers);
	}

	/**
	 * Gets all registered {@link StringSelectMenuHandler}s.
	 *
	 * @see DIH4JDA#addStringSelectMenuHandlers(Map)
	 * @return An immutable {@link Map} which stores the id as the <strong>Key</strong> and
	 * the {@link StringSelectMenuHandler} as the <strong>value</strong>.
	 */
	public @Nonnull Map<List<String>, StringSelectMenuHandler> getStringSelectMenuHandlers() {
		return stringSelectMenuHandlers;
	}

	/**
	 * Gets all registered {@link EntitySelectMenuHandler}s.
	 *
	 * @see DIH4JDA#addEntitySelectMenuHandlers(Map)
	 * @return An immutable {@link Map} which stores the id as the <strong>Key</strong> and
	 * the {@link EntitySelectMenuHandler} as the <strong>values</strong>.
	 */
	public @Nonnull Map<List<String>, EntitySelectMenuHandler> getEntitySelectMenuHandlers() {
		return entitySelectMenuHandlers;
	}

	/**
	 * Binds all {@link ModalHandler}s to their id.
	 * <br>
	 * <pre>{@code
	 * dih4jda.addModalHandlers(Map.of(
	 * 	List.of("apple"), new AppleModalHandler(),
	 * 	List.of("banana"), new BananaModalHandler()
	 * ));
	 * }</pre>
	 * <br>
	 * This is best used in combination with {@link ComponentIdBuilder#build(String, Object...)}.
	 *
	 * @param handlers All {@link ModalHandler}, as a immutable {@link Map}.
	 */
	public void addModalHandlers(Map<List<String>, ModalHandler> handlers) {
		modalHandlers.putAll(handlers);
	}

	/**
	 * Gets all {@link ModalHandler}s.
	 *
	 * @return An immutable {@link Map} which stores the id as the <strong>Key</strong> and
	 * the {@link ModalHandler} as the <strong>Value</strong>.
	 */
	public @Nonnull Map<List<String>, ModalHandler> getModalHandlers() {
		return modalHandlers;
	}
}
