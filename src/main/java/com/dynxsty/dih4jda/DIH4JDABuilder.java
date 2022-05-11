package com.dynxsty.dih4jda;

import com.dynxsty.dih4jda.exceptions.DIH4JDAException;
import com.dynxsty.dih4jda.exceptions.InvalidPackageException;
import net.dv8tion.jda.api.JDA;
import org.reflections.util.ClasspathHelper;

import javax.annotation.Nonnull;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * Builder-System used to build {@link DIH4JDA}.
 */
public class DIH4JDABuilder {
	private final JDA jda;
	private String commandsPackage;
	private Executor executor = ForkJoinPool.commonPool();
	private DIH4JDALogger.Type[] blockedLogTypes;
	private boolean registerOnStartup = true;
	private boolean smartQueuing = true;
	private boolean smartQueueDeleteUnknown = true;

	private DIH4JDABuilder(@Nonnull JDA jda) {
		this.jda = jda;
	}

	/**
	 * Sets the {@link JDA} instance the handler will be used for.
	 *
	 * @param instance The {@link JDA} instance.
	 */
	public static DIH4JDABuilder setJDA(JDA instance) {
		return new DIH4JDABuilder(instance);
	}

	/**
	 * Sets the package that houses all Command classes. DIH4JDA then uses the {@link org.reflections.Reflections} API to "scan" the package for all
	 * command classes.
	 *
	 * @param pack The package's name.
	 */
	@Nonnull
	public DIH4JDABuilder setCommandsPackage(@Nonnull String pack) {
		commandsPackage = pack;
		return this;
	}

	/**
	 * Sets the Executor that will be used to execute all commands.
	 *
	 * @param executor The Executor.
	 */
	@Nonnull
	public DIH4JDABuilder setExecutor(@Nonnull Executor executor) {
		this.executor = executor;
		return this;
	}

	/**
	 * Sets the types of logging that should be disabled.
	 *
	 * @param types All {@link DIH4JDALogger.Type}'s that should be disabled.
	 */
	@Nonnull
	public DIH4JDABuilder disableLogging(DIH4JDALogger.Type... types) {
		if (types == null || types.length < 1) {
			blockedLogTypes = DIH4JDALogger.Type.values();
		} else {
			blockedLogTypes = types;
		}
		return this;
	}

	/**
	 * Whether DIH4JDA should automatically register all interactions on Startup.
	 * A manual registration of all interactions can be executed using {@link DIH4JDA#registerInteractions()}.
	 */
	@Nonnull
	public DIH4JDABuilder disableAutomaticCommandRegistration() {
		registerOnStartup = false;
		return this;
	}

	/**
	 * <b>NOT RECOMMENDED</b> (unless there are some bugs) <br>
	 * This will disable the Smart Queueing functionality.
	 * If Smart Queueing is disabled Global Slash/Context Commands get overridden on each {@link DIH4JDA#registerInteractions()} call,
	 * thus, making Global Commands unusable for about an hour, until they're registered again. <br>
	 * By default, this also deletes unknown commands. This behaviour can be disabled with {@link DIH4JDABuilder#disableSmartQueueRemoveUnknown()}.
	 */
	@Nonnull
	public DIH4JDABuilder disableSmartQueuing() {
		smartQueuing = false;
		return this;
	}

	/**
	 * Disables deletion of unknown/unused commands when using SmartQueue.
	 */
	@Nonnull
	public DIH4JDABuilder disableSmartQueueRemoveUnknown() {
		smartQueueDeleteUnknown = false;
		return this;
	}

	/**
	 * Returns a {@link DIH4JDA} instance that has been validated.
	 *
	 * @return the built, usable {@link DIH4JDA}
	 */
	public DIH4JDA build() throws DIH4JDAException {
		if (Runtime.getRuntime().availableProcessors() == 1) {
			DIH4JDALogger.warn("You are running DIH4JDA on a single core CPU. A special system property was set to disable asynchronous command execution.");
			System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "1");
		}
		if (jda == null) throw new IllegalStateException("JDA instance may not be empty.");
		if (ClasspathHelper.forPackage(commandsPackage).isEmpty()) {
			throw new InvalidPackageException("Package " + commandsPackage + " does not exist.");
		}
		return new DIH4JDA(jda, commandsPackage, registerOnStartup, smartQueuing, smartQueueDeleteUnknown, executor, blockedLogTypes);
	}
}
