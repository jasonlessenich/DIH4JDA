package xyz.dynxsty.dih4jda;

import xyz.dynxsty.dih4jda.config.DIH4JDAConfig;
import xyz.dynxsty.dih4jda.exceptions.DIH4JDAException;
import xyz.dynxsty.dih4jda.exceptions.InvalidPackageException;
import xyz.dynxsty.dih4jda.exceptions.CommandNotRegisteredException;
import xyz.dynxsty.dih4jda.interactions.commands.RegistrationType;
import xyz.dynxsty.dih4jda.util.ClassWalker;
import xyz.dynxsty.dih4jda.util.ClasspathHelper;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.Contract;
import xyz.dynxsty.dih4jda.exceptions.CommandNotRegisteredException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * Builder-System used to build {@link DIH4JDA}.
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
	 */
	@Contract("_ -> new")
	public static @Nonnull DIH4JDABuilder setJDA(@Nonnull JDA instance) {
		return new DIH4JDABuilder(instance);
	}

	/**
	 * Sets the packages that house all Command classes. DIH4JDA then uses the {@link ClassWalker} to "scan" the packages for all
	 * command classes.
	 *
	 * @param pack The packages name.
	 */
	public @Nonnull DIH4JDABuilder setCommandPackages(@Nonnull String... pack) {
		config.setCommandPackages(pack);
		return this;
	}

	/**
	 * Sets the Executor that will be used to execute all commands  and events.
	 *
	 * @param executor The Executor.
	 */
	public @Nonnull DIH4JDABuilder setExecutor(@Nonnull Executor executor) {
		config.setExecutor(executor);
		return this;
	}

	/**
	 * Sets the types of logging that should be disabled.
	 *
	 * @param types All {@link DIH4JDALogger.Type}'s that should be disabled.
	 */
	public @Nonnull DIH4JDABuilder disableLogging(@Nullable DIH4JDALogger.Type... types) {
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
	 * Disables the printing of stacktrace if an exception happens and there is no event listener registered.
	 */
	public @Nonnull DIH4JDABuilder disableStacktracePrinting() {
		config.setDefaultPrintStacktrace(false);
		return this;
	}

	/**
	 * Whether DIH4JDA should automatically register all interactions on each onReady event.
	 * A manual registration of all interactions can be executed using {@link DIH4JDA#registerInteractions()}.
	 */
	public @Nonnull DIH4JDABuilder disableAutomaticCommandRegistration() {
		config.setRegisterOnReady(false);
		return this;
	}

	/**
	 * <b>NOT RECOMMENDED</b> (unless there are some bugs) <br>
	 * This will disable the <b>GLOBAL</b> Smart Queueing functionality.
	 * If the Global SmartQueue is disabled Global Slash/Context Commands get overridden on each {@link DIH4JDA#registerInteractions()} call,
	 * thus, making Global Commands unusable for about an hour, until they're registered again. <br>
	 * By default, this also deletes unknown/unused commands. This behaviour can be disabled with {@link DIH4JDABuilder#disableUnknownCommandDeletion()}.
	 */
	public @Nonnull DIH4JDABuilder setGlobalSmartQueue(boolean enable) {
		config.setGlobalSmartQueue(enable);
		return this;
	}

	/**
	 * This will disable the <b>GUILD</b> Smart Queueing functionality.
	 * If the Guild SmartQueue is disabled Guild Slash/Context Commands get overridden on each {@link DIH4JDA#registerInteractions()} call.
	 * It is <b>RECOMMENDED</b> to disable this functionality for 300+ servers to shorten the start-up time.
	 * By default, this also deletes unknown/unused commands. This behaviour can be disabled with {@link DIH4JDABuilder#disableUnknownCommandDeletion()}.
	 */
	public @Nonnull DIH4JDABuilder setGuildSmartQueue(boolean enable) {
		config.setGuildSmartQueue(enable);
		return this;
	}

	/**
	 * Sets the default {@link RegistrationType} for all Commands.
	 *
	 * @param type The {@link RegistrationType}.
	 */
	public @Nonnull DIH4JDABuilder setDefaultCommandType(@Nonnull RegistrationType type) {
		DIH4JDA.defaultCommandType = type;
		return this;
	}

	/**
	 * Disables deletion of unknown/unused commands when using SmartQueue.
	 */
	public @Nonnull DIH4JDABuilder disableUnknownCommandDeletion() {
		config.setDeleteUnknownCommands(false);
		return this;
	}

	/**
	 * Disables the {@link CommandNotRegisteredException} getting thrown
	 * for unregistered commands.
	 */
	public @Nonnull DIH4JDABuilder disableUnregisteredCommandException() {
		config.setThrowUnregisteredException(false);
		return this;
	}

	/**
	 * Returns a {@link DIH4JDA} instance that has been validated.
	 *
	 * @return the built, usable {@link DIH4JDA}
	 */
	public @Nonnull DIH4JDA build() throws DIH4JDAException {
		if (Runtime.getRuntime().availableProcessors() == 1) {
			DIH4JDALogger.warn("You are running DIH4JDA on a single core CPU. A special system property was set to disable asynchronous command execution.");
			System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "1");
		}
		for (String pkg : config.getCommandPackages()) {
			if (pkg.isBlank() || pkg.isEmpty()) {
				throw new InvalidPackageException("Commands package cannot be empty or blank.");
			}
			if (ClasspathHelper.forPackage(pkg).isEmpty()) {
				throw new InvalidPackageException("Package '" + pkg + "' does not exist.");
			}
		}
		config.setJDA(jda);
		return new DIH4JDA(config);
	}
}
