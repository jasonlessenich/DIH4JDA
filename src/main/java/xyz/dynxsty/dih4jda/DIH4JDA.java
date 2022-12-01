package xyz.dynxsty.dih4jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.dynxsty.dih4jda.config.DIH4JDAConfig;
import xyz.dynxsty.dih4jda.events.DIH4JDAEventListener;
import xyz.dynxsty.dih4jda.exceptions.DIH4JDAException;
import xyz.dynxsty.dih4jda.exceptions.InvalidConfigurationException;
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
 * <p><b>Getting Started</b></p>
 *
 * Creating a new DIH4JDA instance is fairly easy:
 *
 * <pre>{@code
 * DIH4JDA dih4jda = DIH4JDABuilder
 *         .setJDA(jda) // Your JDA instance
 *         .build();
 * }</pre>
 *
 * Now, you get to decide how you want your commands to be registered:
 *
 * <p><b>Manual Command Registration</b></p>
 *
 * To manually register commands, use the following methods, <b>AFTER</b> you've <code>.build();</code> your DIH4JDA instance, like that:
 *
 * <pre>{@code
 * DIH4JDA dih4jda = DIH4JDABuilder
 *         .setJDA(jda) // Your JDA instance
 *         .build();
 * dih4jda.addSlashCommands(new PingCommand(), new HelloWorldCommand());
 * dih4jda.addContextMenus(new PingUserContext(), new HelloWorldMessageContext());
 * }</pre>
 *
 * <p><b>Automatic Command Registration</b></p>
 *
 * Alternatively, you can specify packages on the {@link DIH4JDABuilder} instance which will be scanned for all classes that extend one of the following classes:
 *
 * <ul>
 *     <li>{@link SlashCommand}</li>
 *     <li>{@link ContextCommand.User}</li>
 *     <li>{@link ContextCommand.Message}</li>
 * </ul>
 *
 * <pre>{@code
 * DIH4JDA dih4jda = DIH4JDABuilder
 *         .setJDA(jda) // Your JDA instance
 *         .setCommandPackages("xyz.dynxsty.bot.commands") // OPTIONAL: The package(s) that contains all your commands
 *         .build();
 * }</pre>
 *
 * Upon calling <code>.build();</code>, the bot will register all commands that are in the specified package(s).
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
	 * Constructs a new DIH4JDA instance using the specified {@link DIH4JDAConfig}.
	 * It is <b>highly recommended</b> to use the {@link DIH4JDABuilder} instead.
     *
     * @param config The instance's {@link DIH4JDAConfig configuration}.
	 * @throws DIH4JDAException if the given {@link DIH4JDAConfig} is invalid.
	 */
	public DIH4JDA(@Nonnull DIH4JDAConfig config) throws DIH4JDAException {
		validateConfig(config);
		this.config = config;
		DIH4JDALogger.blockedLogTypes = config.getBlockedLogTypes();
		this.handler = new InteractionHandler(this);
		this.config.getJda().addEventListener(this, handler);
		listeners = new HashSet<>();
	}

	/**
	 * Ran once the {@link JDA} instance fires the {@link ReadyEvent}.
	 *
	 * @param event The {@link ReadyEvent} that was fired.
	 */
	@Override
	public void onReady(@Nonnull ReadyEvent event) {
		if (config.getCommandsPackages() == null) {
			return;
		}
		if (config.isRegisterOnReady() && handler != null) {
			handler.registerInteractions();
		}
	}

	/**
	 * Sets the default {@link RegistrationType} for all
	 * {@link xyz.dynxsty.dih4jda.interactions.commands.application.ApplicationCommand Application Commands}.
	 * This is set to {@link RegistrationType#GLOBAL} if not set otherwise.
	 *
	 * @param type The {@link RegistrationType}.
	 */
	public static void setDefaultRegistrationType(@Nonnull RegistrationType type) {
		DIH4JDA.defaultRegistrationType = type;
	}

	/**
	 * Gets the default {@link RegistrationType}.
	 *
	 * @return the {@link RegistrationType}.
	 */
	@Nonnull
	public static RegistrationType getDefaultRegistrationType() {
		return defaultRegistrationType;
	}

	/**
	 * Registers all Interactions and replaces the old ones.
	 * Please note that global commands may need up to an hour before they're fully registered.
	 */
	public void registerInteractions() {
		if (handler != null) {
			handler.registerInteractions();
		}
	}

	/**
	 * The {@link DIH4JDAConfig} that is used by this specific {@link DIH4JDA} instance.
	 *
	 * @return The instance's {@link DIH4JDAConfig configuration}.
	 */
	@Nonnull
	public DIH4JDAConfig getConfig() {
		return config;
	}

	/**
	 * Allows to register {@link DIH4JDAEventListener event listener} classes.
	 *
	 * @param classes Implementations of {@link DIH4JDAEventListener}.
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
	 * Gets all {@link DIH4JDAEventListener}s that were previously added using {@link DIH4JDA#addEventListener(Object...)}.
	 *
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
	@Nonnull
	public JDA getJDA() {
		return config.getJda();
	}

	/**
	 * Manually registers {@link SlashCommand}s.
	 *
	 * @param commands An array of commands to register.
	 */
	public void addSlashCommands(@Nonnull SlashCommand... commands) {
		handler.slashCommands.addAll(List.of(commands));
	}

	/**
	 * Manually registers {@link ContextCommand}s.
	 *
	 * @param commands An array of commands to register.
	 */
	public void addContextCommands(@Nonnull ContextCommand<?>... commands) {
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
	public final void addButtonMappings(@Nonnull IdMapping<ButtonHandler>... mappings) {
		validateMappings(mappings);
		buttonMappings = mappings;
	}

	/**
	 * Gets all {@link ButtonHandler}s.
	 *
	 * @return An {@link IdMapping} array which contains the never-null ids and handlers.
	 */
	@Nonnull
	public final IdMapping<ButtonHandler>[] getButtonMappings() {
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
	 * @param mappings All {@link StringSelectMenuHandler}, as an array of {@link IdMapping}.
	 * @see DIH4JDA#addEntitySelectMenuMappings(IdMapping[])
	 */
	@SafeVarargs
	public final void addStringSelectMenuMappings(@Nonnull IdMapping<StringSelectMenuHandler>... mappings) {
		validateMappings(mappings);
		stringSelectMenuMappings = mappings;
	}

	/**
	 * Gets all registered {@link StringSelectMenuHandler}s.
	 *
	 * @return An {@link IdMapping} array which contains the never-null ids and handlers.
	 * @see DIH4JDA#addStringSelectMenuMappings(IdMapping[])
	 */
	@Nonnull
	public final IdMapping<StringSelectMenuHandler>[] getStringSelectMenuMappings() {
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
	 * @param mappings All {@link EntitySelectMenuHandler}, as an array of {@link IdMapping}.
	 * @see DIH4JDA#addStringSelectMenuMappings(IdMapping[])
	 */
	@SafeVarargs
	public final void addEntitySelectMenuMappings(@Nonnull IdMapping<EntitySelectMenuHandler>... mappings) {
		validateMappings(mappings);
		entitySelectMenuMappings = mappings;
	}

	/**
	 * Gets all registered {@link EntitySelectMenuHandler}s.
	 *
	 * @return An {@link IdMapping} array which contains the never-null ids and handlers.
	 * @see DIH4JDA#addEntitySelectMenuMappings(IdMapping[])
	 */
	@Nonnull
	public final IdMapping<EntitySelectMenuHandler>[] getEntitySelectMenuMappings() {
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
	public final void addModalMappings(@Nonnull IdMapping<ModalHandler>... mappings) {
		validateMappings(mappings);
		modalMappings = mappings;
	}

	/**
	 * Gets all {@link ModalHandler}s.
	 *
	 * @return An {@link IdMapping} array which contains the never-null ids and handlers.
	 */
	@Nonnull
	public final IdMapping<ModalHandler>[] getModalMappings() {
		return modalMappings;
	}

	/**
	 * Validates the specified {@link IdMapping}s and throws an {@link IllegalArgumentException}
	 * if they're invalid.
	 *
	 * @param mappings The {@link IdMapping}-array to validate.
	 * @param <T> The mappings' type.
	 */
	@SafeVarargs
	private <T> void validateMappings(@Nonnull IdMapping<T>... mappings) {
		for (IdMapping<T> mapping : mappings) {
			if (mapping.getIds().length == 0) {
				throw new IllegalArgumentException("Ids may not be empty or null!");
			}
		}
	}

	/**
	 * Validates the specified {@link DIH4JDAConfig} and throws an {@link InvalidConfigurationException}
	 * if the config is invalid.
	 *
	 * @param config The {@link DIH4JDAConfig} to validate.
	 * @throws DIH4JDAException If specified the config is invalid.
	 */
	private void validateConfig(@Nonnull DIH4JDAConfig config) throws DIH4JDAException {
		if (config.getJda() == null) {
			throw new InvalidConfigurationException("JDA instance may not be null!");
		}
		if (config.getBlockedLogTypes() == null) {
			throw new InvalidConfigurationException("Blocked Log Types may not be null!");
		}
		if (config.getCommandsPackages() == null) {
			throw new InvalidConfigurationException("Command Packages may not be null!");
		}
		if (config.getExecutor() == null) {
			throw new InvalidConfigurationException("Executor may not be null!");
		}
	}
}
