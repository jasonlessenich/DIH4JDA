package xyz.dynxsty.dih4jda;

import net.dv8tion.jda.api.JDA;
import xyz.dynxsty.dih4jda.config.DIH4JDAConfig;
import xyz.dynxsty.dih4jda.exceptions.CommandNotRegisteredException;
import xyz.dynxsty.dih4jda.exceptions.DIH4JDAException;
import xyz.dynxsty.dih4jda.exceptions.InvalidPackageException;
import xyz.dynxsty.dih4jda.util.ClassWalker;
import xyz.dynxsty.dih4jda.util.ClasspathHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Executor;

/**
 * Builder class used to instantiate a new {@link DIH4JDA} instance. 
 * @see DIH4JDA
 */
public class DIH4JDABuilder {
	private final JDA jda;
	private final DIH4JDAConfig config;

	private DIH4JDABuilder(@Nonnull JDA jda) {
		this.config = new DIH4JDAConfig();
		this.jda = jda;
	}

	/**
	 * Sets the {@link JDA} instance the handler will be used for.
	 *
	 * @param instance The {@link JDA} instance.
	 * @return The {@link DIH4JDABuilder} for chaining convenience.
	 */
	@Nonnull
	public static DIH4JDABuilder setJDA(@Nonnull JDA instance) {
		return new DIH4JDABuilder(instance);
	}

	/**
	 * Allows to specify package(s) that house all {@link xyz.dynxsty.dih4jda.interactions.commands.application.ApplicationCommand}s classes. DIH4JDA then uses the {@link ClassWalker} to "scan" for those classes.
	 *
	 * @param pack The packages.
	 * @return The {@link DIH4JDABuilder} for chaining convenience.
	 */
	@Nonnull
	public DIH4JDABuilder setCommandPackages(@Nonnull String... pack) {
		config.setCommandsPackages(pack);
		return this;
	}

	/**
	 * Allows to specify a custom {@link Executor} that will be used to execute all commands and events.
	 *
	 * @param executor The custom {@link Executor}.
	 * @return The {@link DIH4JDABuilder} for chaining convenience.
	 */
	@Nonnull
	public DIH4JDABuilder setExecutor(@Nonnull Executor executor) {
		config.setExecutor(executor);
		return this;
	}

	/**
	 * Sets the types of logging that should be disabled.
	 *
	 * @param types All {@link DIH4JDALogger.Type}'s that should be disabled.
	 * @return The {@link DIH4JDABuilder} for chaining convenience.
	 * @deprecated Use {@link DIH4JDALogger#disableLogging(DIH4JDALogger.Type...)} instead. <b>Will be removed in 2.0</b>
	 */
	@Nonnull
	@Deprecated(forRemoval = true)
	public DIH4JDABuilder disableLogging(@Nullable DIH4JDALogger.Type... types) {
		DIH4JDALogger.Type[] blocked;
		if (types == null || types.length < 1) {
			blocked = DIH4JDALogger.Type.values();
		} else {
			blocked = types;
		}
		config.setBlockedLogTypes(blocked);
		return this;
	}

	/**
	 * Disables stacktrace-printing for all raised exceptions that were not caught by an
	 * {@link xyz.dynxsty.dih4jda.events.DIH4JDAEventListener}.
	 *
	 * @return The {@link DIH4JDABuilder} for chaining convenience.
	 */
	@Nonnull
	public DIH4JDABuilder disableStacktracePrinting() {
		config.setDefaultPrintStacktrace(false);
		return this;
	}

	/**
	 * Whether DIH4JDA should automatically register all interactions on each onReady event.
	 * A manual registration of all interactions can be executed using {@link DIH4JDA#registerInteractions()}.
	 *
	 * @return The {@link DIH4JDABuilder} for chaining convenience.
	 */
	@Nonnull
	public DIH4JDABuilder disableAutomaticCommandRegistration() {
		config.setRegisterOnReady(false);
		return this;
	}

	/**
	 * <b>NOT RECOMMENDED</b> (unless there are some bugs) <br>
	 * This will disable the <b>GLOBAL</b> Smart Queueing functionality.
	 * If the Global SmartQueue is disabled Global Slash/Context Commands get overridden on each {@link DIH4JDA#registerInteractions()} call,
	 * thus, making Global Commands unusable for about an hour, until they're registered again. <br>
	 * By default, this also deletes unknown/unused commands. This behaviour can be disabled with {@link DIH4JDABuilder#disableUnknownCommandDeletion()}.
	 *
	 * @param enable a {@link Boolean} that is true if the {@link SmartQueue} should be enabled for global commands.
	 * @return The {@link DIH4JDABuilder} for chaining convenience.
	 */
	@Nonnull
	public DIH4JDABuilder setGlobalSmartQueue(boolean enable) {
		config.setGlobalSmartQueue(enable);
		return this;
	}

	/**
	 * This will disable the <b>GUILD</b> Smart Queueing functionality.
	 * If the Guild SmartQueue is disabled Guild Slash/Context Commands get overridden on each {@link DIH4JDA#registerInteractions()} call.
	 * It is <b>RECOMMENDED</b> to disable this functionality for 300+ servers to shorten the start-up time.
	 * By default, this also deletes unknown/unused commands. This behaviour can be disabled with {@link DIH4JDABuilder#disableUnknownCommandDeletion()}.
	 *
	 * @param enable a {@link Boolean} that is true if the {@link SmartQueue} should be enabled for guild commands.
	 * @return The {@link DIH4JDABuilder} for chaining convenience.
	 */
	@Nonnull
	public DIH4JDABuilder setGuildSmartQueue(boolean enable) {
		config.setGuildSmartQueue(enable);
		return this;
	}

	/**
	 * Disables the deletion of unknown/unused commands while using SmartQueue.
	 *
	 * @return The {@link DIH4JDABuilder} for chaining convenience.
	 */
	@Nonnull
	public DIH4JDABuilder disableUnknownCommandDeletion() {
		config.setDeleteUnknownCommands(false);
		return this;
	}

	/**
	 * Disables the {@link CommandNotRegisteredException} getting thrown
	 * for unregistered commands.
	 *
	 * @return The {@link DIH4JDABuilder} for chaining convenience.
	 */
	@Nonnull
	public DIH4JDABuilder disableUnregisteredCommandException() {
		config.setThrowUnregisteredException(false);
		return this;
	}

	/**
	 * Returns a validated {@link DIH4JDA} instance.
	 *
	 * @return The built, usable {@link DIH4JDA}
	 * @throws DIH4JDAException If anything was wrong with your configuration.
	 */
	@Nonnull
	public DIH4JDA build() throws DIH4JDAException {
		if (Runtime.getRuntime().availableProcessors() == 1) {
			DIH4JDALogger.warn("You are running DIH4JDA on a single core CPU. A special system property was set to disable asynchronous command execution.");
			System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "1");
		}
		for (String pkg : config.getCommandsPackages()) {
			if (pkg.isBlank() || pkg.isEmpty()) {
				throw new InvalidPackageException("Commands package cannot be empty or blank.");
			}
			if (ClasspathHelper.forPackage(pkg).isEmpty()) {
				throw new InvalidPackageException("Package '" + pkg + "' does not exist.");
			}
		}
		config.setJda(jda);
		return new DIH4JDA(config);
	}
}
