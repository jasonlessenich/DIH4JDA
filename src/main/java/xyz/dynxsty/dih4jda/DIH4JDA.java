package xyz.dynxsty.dih4jda;

import lombok.AccessLevel;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.dynxsty.dih4jda.config.DIH4JDAConfig;
import xyz.dynxsty.dih4jda.events.DIH4JDAEventListener;
import xyz.dynxsty.dih4jda.exceptions.DIH4JDAException;
import xyz.dynxsty.dih4jda.interactions.commands.application.BaseApplicationCommand;
import xyz.dynxsty.dih4jda.interactions.commands.application.ContextCommand;
import xyz.dynxsty.dih4jda.interactions.commands.application.RegistrationType;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;
import xyz.dynxsty.dih4jda.interactions.commands.text.TextCommand;
import xyz.dynxsty.dih4jda.interactions.components.ButtonHandler;
import xyz.dynxsty.dih4jda.interactions.components.EntitySelectMenuHandler;
import xyz.dynxsty.dih4jda.interactions.components.IdMapping;
import xyz.dynxsty.dih4jda.interactions.components.ModalHandler;
import xyz.dynxsty.dih4jda.interactions.components.StringSelectMenuHandler;
import xyz.dynxsty.dih4jda.util.Checks;
import xyz.dynxsty.dih4jda.util.ComponentIdBuilder;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
	@Getter(AccessLevel.PUBLIC)
	private static RegistrationType defaultRegistrationType = RegistrationType.GLOBAL;

	// Component Handler
	/**
	 * An {@link IdMapping} array that contains all {@link ButtonHandler}s that are registered to this instance.
	 */
	@Getter(AccessLevel.PUBLIC)
	private IdMapping<ButtonHandler>[] buttonMappings = null;
	/**
	 * An {@link IdMapping} array that contains all {@link StringSelectMenuHandler}s that are registered to this instance.
	 */
	@Getter(AccessLevel.PUBLIC)
	private IdMapping<StringSelectMenuHandler>[] stringSelectMenuMappings = null;
	/**
	 * An {@link IdMapping} array that contains all {@link EntitySelectMenuHandler}s that are registered to this instance.
	 */
	@Getter(AccessLevel.PUBLIC)
	private IdMapping<EntitySelectMenuHandler>[] entitySelectMenuMappings = null;
	/**
	 * An {@link IdMapping} array that contains all {@link ModalHandler}s that are registered to this instance.
	 */
	@Getter(AccessLevel.PUBLIC)
	private IdMapping<ModalHandler>[] modalMappings = null;
	/**
	 * The {@link DIH4JDAConfig} instance that is linked to this specific {@link DIH4JDA} instance.
	 */
	@Getter(AccessLevel.PUBLIC)
	private final DIH4JDAConfig config;
	/**
	 * A set of all {@link DIH4JDAEventListener}s that are registered to this instance.
	 */
	@Getter(AccessLevel.PUBLIC)
	private final Set<DIH4JDAEventListener> eventListeners;
	private final InteractionHandler handler;

	// TODO: Docs
	private String globalPrefix = "!";

	// TODO: Docs
	private final Map<Long, String> guildPrefixOverrides = new HashMap<>();

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
		eventListeners = new HashSet<>();
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
			handler.registerCommands();
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
	 * Registers all Interactions and replaces the old ones.
	 * Please note that global commands may need up to an hour before they're fully registered.
	 */
	public void registerInteractions() {
		if (handler != null) {
			handler.registerCommands();
		}
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
				eventListeners.add(adapter);
			} catch (ClassCastException e) {
				throw new IllegalArgumentException("Listener classes must implement DIH4JDAEventListener!");
			}
		}
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

	// TODO: Docs
	public void addTextCommands(@Nonnull TextCommand... commands) {
		handler.textCommands.addAll(List.of(commands));
	}

	public Set<TextCommand> getTextCommands() {
		return handler.textCommands;
	}

	public Map<String, List<TextCommand>> getTextCommandsCategorized(@Nonnull String uncategorizedName) {
		final Map<String, List<TextCommand>> categorized = new HashMap<>();
		handler.textCommands.forEach(c -> {
			final String commandCategory = c.getCommandData().getCategory();
			final String category = commandCategory == null || commandCategory.isEmpty() ? uncategorizedName : commandCategory;
			if (categorized.containsKey(category)) {
				final List<TextCommand> mapped = categorized.get(category);
				mapped.add(c);
				categorized.put(category, mapped);
			} else {
				final List<TextCommand> command = new ArrayList<>();
				command.add(c);
				categorized.put(category, command);
			}
		});
		return categorized;
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

	// TODO: Docs
	public String getGlobalPrefix() {
		return globalPrefix;
	}

	// TODO: Docs
	public void setGlobalPrefix(String globalPrefix) {
		this.globalPrefix = globalPrefix;
	}

	// TODO: Docs
	public Map<Long, String> getGuildPrefixOverrides() {
		return guildPrefixOverrides;
	}

	// TODO: Docs
	public void addGuildPrefixOverride(Long guildId, String prefix) {
		guildPrefixOverrides.put(guildId, prefix);
	}

	// TODO: Docs
	public String removeGuildPrefixOverride(Long guildId) {
		return guildPrefixOverrides.remove(guildId);
	}

	// TODO: Docs
	public String getEffectivePrefix(Guild guild) {
		if (guild == null) return globalPrefix;
		String prefix = guildPrefixOverrides.get(guild.getIdLong());
		if (prefix == null) return globalPrefix;
		else return prefix;
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
	 * Validates the specified {@link DIH4JDAConfig} and throws an {@link IllegalArgumentException}
	 * if the config is invalid.
	 *
	 * @param config The {@link DIH4JDAConfig} to validate.
	 * @throws IllegalArgumentException If the config is invalid.
	 */
	private void validateConfig(@Nonnull DIH4JDAConfig config) {
		Checks.notNull(config.getJda(), "JDA instance");
		Checks.notNull(config.getBlockedLogTypes(), "Blocked Log Types");
		Checks.notNull(config.getCommandsPackages(), "Command Packages");
		Checks.notNull(config.getExecutor(), "Executor");
	}
}
