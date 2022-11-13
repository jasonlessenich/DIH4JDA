package xyz.dynxsty.dih4jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.dynxsty.dih4jda.config.DIH4JDAConfig;
import xyz.dynxsty.dih4jda.events.DIH4JDAEventListener;
import xyz.dynxsty.dih4jda.exceptions.DIH4JDAException;
import xyz.dynxsty.dih4jda.interactions.commands.application.BaseApplicationCommand;
import xyz.dynxsty.dih4jda.interactions.commands.application.ContextCommand;
import xyz.dynxsty.dih4jda.interactions.commands.application.RegistrationType;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;
import xyz.dynxsty.dih4jda.interactions.components.ButtonHandler;
import xyz.dynxsty.dih4jda.interactions.components.EntitySelectMenuHandler;
import xyz.dynxsty.dih4jda.interactions.components.IdMapping;
import xyz.dynxsty.dih4jda.interactions.components.ModalHandler;
import xyz.dynxsty.dih4jda.interactions.components.StringSelectMenuHandler;
import xyz.dynxsty.dih4jda.util.ComponentIdBuilder;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
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
	 * This can be overridden using {@link BaseApplicationCommand#setRegistrationType(RegistrationType)}
	 */
	private static RegistrationType defaultRegistrationType = RegistrationType.GLOBAL;

	// Component Handler
	private IdMapping<ButtonHandler>[] buttonMappings = null;
	private IdMapping<StringSelectMenuHandler>[] stringSelectMenuMappings = null;
	private IdMapping<EntitySelectMenuHandler>[] entitySelectMenuMappings = null;
	private IdMapping<ModalHandler>[] modalMappings = null;

	private final DIH4JDAConfig config;
	private final Set<DIH4JDAEventListener> listeners;
	private final InteractionHandler handler;

	/**
	 * Constructs a new DIH4JDA instance
	 *
	 * @param config The instance's configuration.
	 */
	protected DIH4JDA(@Nonnull DIH4JDAConfig config) throws DIH4JDAException {
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
	 * Sets the default {@link RegistrationType} for all Commands.
	 * This is set to {@link RegistrationType#GLOBAL} if not set otherwise.
	 *
	 * @param type The {@link RegistrationType}.
	 */
	public static void setDefaultRegistrationType(RegistrationType type) {
		DIH4JDA.defaultRegistrationType = type;
	}

	public static RegistrationType getDefaultRegistrationType() {
		return defaultRegistrationType;
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
	 * Gets the {@link JDA} instance {@link DIH4JDA} uses.
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
	 * Binds all {@link ButtonHandler}s to their id using {@link IdMapping}
	 * <br>
	 * <pre>{@code
	 * dih4jda.addButtonMappings(
	 *     IdMapping.of(new PingCommand(), "ping", "hello-world"),
	 *     IdMapping.of(new DummyCommand(), "dummy", "test")
	 * );
	 * }</pre>
	 * <br>
	 * This is best used in combination with {@link ComponentIdBuilder#build(String, Object...)}.
	 *
	 * @param mappings All {@link ButtonHandler}, as an array of {@link IdMapping}.
	 */
	@SafeVarargs
	public final void addButtonMappings(IdMapping<ButtonHandler>... mappings) {
		checkHandlers(mappings);
		buttonMappings = mappings;
	}

	/**
	 * Gets all {@link ButtonHandler}s.
	 *
	 * @return An {@link IdMapping} array which contains the never-null ids and handlers.
	 */
	public final @Nonnull IdMapping<ButtonHandler>[] getButtonMappings() {
		return buttonMappings;
	}

	/**
	 * Binds all {@link StringSelectMenuHandler}s to their id.
	 * <br>
	 * <pre>{@code
	 * dih4jda.addStringSelectMenuMappings(
	 *     IdMapping.of(new PingCommand(), "ping", "hello-world"),
	 *     IdMapping.of(new DummyCommand(), "dummy", "test")
	 * );
	 * }</pre>
	 * <br>
	 * This is best used in combination with {@link ComponentIdBuilder#build(String, Object...)}.
	 *
	 * @see DIH4JDA#addEntitySelectMenuMappings(IdMapping[])
	 * @param mappings All {@link StringSelectMenuHandler}, as an array of {@link IdMapping}.
	 */
	@SafeVarargs
	public final void addStringSelectMenuMappings(IdMapping<StringSelectMenuHandler>... mappings) {
		checkHandlers(mappings);
		stringSelectMenuMappings = mappings;
	}

	/**
	 * Gets all registered {@link StringSelectMenuHandler}s.
	 *
	 * @see DIH4JDA#addStringSelectMenuMappings(IdMapping[])
	 * @return An {@link IdMapping} array which contains the never-null ids and handlers.
	 */
	public final @Nonnull IdMapping<StringSelectMenuHandler>[] getStringSelectMenuMappings() {
		return stringSelectMenuMappings;
	}

	/**
	 * Binds all {@link EntitySelectMenuHandler}s to their id,
	 * <br>
	 * <pre>{@code
	 * dih4jda.addEntitySelectMenuMappings(
	 *     IdMapping.of(new PingCommand(), "ping", "hello-world"),
	 *     IdMapping.of(new DummyCommand(), "dummy", "test")
	 * );
	 * }</pre>
	 * <br>
	 * This is best used in combination with {@link ComponentIdBuilder#build(String, Object...)}.
	 *
	 * @see DIH4JDA#addStringSelectMenuMappings(IdMapping[])
	 * @param mappings All {@link EntitySelectMenuHandler}, as an array of {@link IdMapping}.
	 */
	@SafeVarargs
	public final void addEntitySelectMenuMappings(IdMapping<EntitySelectMenuHandler>... mappings) {
		checkHandlers(mappings);
		entitySelectMenuMappings = mappings;
	}

	/**
	 * Gets all registered {@link EntitySelectMenuHandler}s.
	 *
	 * @see DIH4JDA#addEntitySelectMenuMappings(IdMapping[])
	 * @return An {@link IdMapping} array which contains the never-null ids and handlers.
	 */
	public final @Nonnull IdMapping<EntitySelectMenuHandler>[] getEntitySelectMenuMappings() {
		return entitySelectMenuMappings;
	}

	/**
	 * Binds all {@link ModalHandler}s to their id.
	 * <br>
	 * <pre>{@code
	 * dih4jda.addModalMappings(
	 *     IdMapping.of(new PingCommand(), "ping", "hello-world"),
	 *     IdMapping.of(new DummyCommand(), "dummy", "test")
	 * );
	 * }</pre>
	 * <br>
	 * This is best used in combination with {@link ComponentIdBuilder#build(String, Object...)}.
	 *
	 * @param mappings All {@link ModalHandler}, as an array of {@link IdMapping}.
	 */
	@SafeVarargs
	public final void addModalMappings(IdMapping<ModalHandler>... mappings) {
		checkHandlers(mappings);
		modalMappings = mappings;
	}

	/**
	 * Gets all {@link ModalHandler}s.
	 *
	 * @return An {@link IdMapping} array which contains the never-null ids and handlers.
	 */
	public final @Nonnull IdMapping<ModalHandler>[] getModalMappings() {
		return modalMappings;
	}

	@SafeVarargs
	private <T> void checkHandlers(@Nonnull IdMapping<T>... mappings) {
		for (IdMapping<T> mapping : mappings) {
			if (mapping.getHandler() == null) {
				throw new IllegalArgumentException("Handler may not be null!");
			}
			if (mapping.getIds() == null || mapping.getIds().length == 0) {
				throw new IllegalArgumentException("Ids may not be empty or null!");
			}
		}
	}
}
