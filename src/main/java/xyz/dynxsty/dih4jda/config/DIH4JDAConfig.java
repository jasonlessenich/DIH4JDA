package xyz.dynxsty.dih4jda.config;

import xyz.dynxsty.dih4jda.DIH4JDALogger;
import net.dv8tion.jda.api.JDA;
import xyz.dynxsty.dih4jda.DIH4JDA;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * Simple data class which represents the config of a single {@link DIH4JDA}
 * instance.
 */
public class DIH4JDAConfig {
	private JDA jda;
	private String[] commandsPackages = new String[]{};
	private Set<DIH4JDALogger.Type> blockedLogTypes = new HashSet<>();
	private boolean registerOnReady = true;
	private boolean globalSmartQueue = true;
	private boolean guildSmartQueue = true;
	private boolean deleteUnknownCommands = true;
	private boolean throwUnregisteredException = true;
	//We have to find a better name for this boolean
	private boolean defaultPrintStacktrace = true;
	private Executor executor = ForkJoinPool.commonPool();

	public JDA getJDA() {
		return jda;
	}

	public void setJDA(JDA jda) {
		this.jda = jda;
	}

	public String[] getCommandPackages() {
		return commandsPackages;
	}

	public void setCommandPackages(String... commandsPackage) {
		this.commandsPackages = commandsPackage;
	}

	public Set<DIH4JDALogger.Type> getBlockedLogTypes() {
		return blockedLogTypes;
	}

	public void setBlockedLogTypes(Set<DIH4JDALogger.Type> blockedLogTypes) {
		this.blockedLogTypes = blockedLogTypes;
	}

	public boolean isRegisterOnReady() {
		return registerOnReady;
	}

	public void setRegisterOnReady(boolean registerOnReady) {
		this.registerOnReady = registerOnReady;
	}

	public boolean isGlobalSmartQueue() {
		return globalSmartQueue;
	}

	public void setGlobalSmartQueue(boolean globalSmartQueue) {
		this.globalSmartQueue = globalSmartQueue;
	}

	public boolean isGuildSmartQueue() {
		return guildSmartQueue;
	}

	public void setGuildSmartQueue(boolean guildSmartQueue) {
		this.guildSmartQueue = guildSmartQueue;
	}

	public boolean isDeleteUnknownCommands() {
		return deleteUnknownCommands;
	}

	public void setDeleteUnknownCommands(boolean deleteUnknownCommands) {
		this.deleteUnknownCommands = deleteUnknownCommands;
	}

	public boolean isDefaultPrintStacktrace() {
		return defaultPrintStacktrace;
	}

	public void setDefaultPrintStacktrace(boolean defaultPrintStacktrace) {
		this.defaultPrintStacktrace = defaultPrintStacktrace;
	}

	public Executor getExecutor() {
		return executor;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	public boolean isThrowUnregisteredException() {
		return throwUnregisteredException;
	}

	public void setThrowUnregisteredException(boolean throwUnregisteredException) {
		this.throwUnregisteredException = throwUnregisteredException;
	}
}
