package xyz.dynxsty.dih4jda.config;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.DIH4JDALogger;
import xyz.dynxsty.dih4jda.events.interactions.TextCommandEvent;
import xyz.dynxsty.dih4jda.interactions.commands.text.TextCommand;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Simple data class which represents {@link DIH4JDA}'s configuration.
 */
public class DIH4JDAConfig {
    private JDA jda;
    private String[] commandsPackages = new String[]{};
    private DIH4JDALogger.Type[] blockedLogTypes = new DIH4JDALogger.Type[]{};
    private boolean registerOnReady = true;
    private boolean globalSmartQueue = true;
    private boolean guildSmartQueue = true;
    private boolean deleteUnknownCommands = true;
    private boolean throwUnregisteredException = true;
    private boolean defaultPrintStacktrace = true;
    private boolean enableHelpCommand = true;
    private Executor executor = ForkJoinPool.commonPool();
    private List<String> helpCommandNames = List.of("help");
    private BiConsumer<TextCommandEvent, List<TextCommand>> helpCommandConsumer = (event, commands) -> {
        String prefix = event.getDIH4JDA().getEffectivePrefix(event.getGuild());
        Map<String, List<TextCommand>> categorizedCommand = event.getDIH4JDA().getTextCommandsCategorized();
		// build embed
		EmbedBuilder builder = new EmbedBuilder()
				.setTitle("Help List")
				.setColor(Color.blue)
				.setTimestamp(Instant.now());
	    categorizedCommand.forEach((category, list) -> {
		    builder.appendDescription(String.format("%n**%s**%n", category));
		    list.forEach(c -> builder.appendDescription(String.format("`%s%s`%s%n", prefix, c.getName(),
                    c.getDescription() == null ? "" : ": " + c.getDescription())));
	    });
		event.getMessage().replyEmbeds(builder.build()).queue();
    };

    /**
     * Creates a default instance.
     */
    public DIH4JDAConfig() {
    }

    /**
     * Gets you the {@link JDA} instance that {@link DIH4JDA} uses.
     *
     * @return the {@link JDA} instance.
     */
    public JDA getJDA() {
        return jda;
    }

    /**
     * Sets the {@link JDA} instance that {@link DIH4JDA} should use.
     *
     * @param jda the {@link JDA} instance to use.
     */
    public void setJDA(JDA jda) {
        this.jda = jda;
    }

    /**
     * Gets the packages where the commands are.<br>
     * <b>Standard:</b> {@code new String[]{}}
     *
     * @return the package names as an {@link String} array
     */
    public String[] getCommandPackages() {
        return commandsPackages;
    }

    /**
     * Sets one or more packages.
     *
     * @param commandsPackage the packages where the commands are located.
     */
    public void setCommandPackages(String... commandsPackage) {
        this.commandsPackages = commandsPackage;
    }

    /**
     * Gets you all {@link DIH4JDALogger.Type}s that are not getting logged.<br>
     * <b>Standard:</b> {@code new DIH4JDALogger.Type[]{}}
     *
     * @return the {@link DIH4JDALogger.Type}s as an array.
     */
    public DIH4JDALogger.Type[] getBlockedLogTypes() {
        return blockedLogTypes;
    }

    /**
     * Sets one or more {@link DIH4JDALogger.Type}s that should not be getting logged.
     *
     * @param blockedLogTypes the {@link DIH4JDALogger.Type} to block.
     */
    public void setBlockedLogTypes(DIH4JDALogger.Type[] blockedLogTypes) {
        this.blockedLogTypes = blockedLogTypes;
    }

    /**
     * True if all commands should be registered on JDAs {@link net.dv8tion.jda.api.events.session.ReadyEvent}.<br>
     * <b>Standard:</b> {@code true}
     *
     * @return true if they are getting registered, otherwise false.
     */
    public boolean isRegisterOnReady() {
        return registerOnReady;
    }

    /**
     * True if all commands should be registered on JDAs {@link net.dv8tion.jda.api.events.session.ReadyEvent}.
     *
     * @param registerOnReady true if they should be getting registered, otherwise false.
     */
    public void setRegisterOnReady(boolean registerOnReady) {
        this.registerOnReady = registerOnReady;
    }

    /**
     * True if {@link xyz.dynxsty.dih4jda.SmartQueue} is enabled for global commands.<br>
     * <b>Standard:</b> {@code true}
     *
     * @return true if it is enabled, otherwise false.
     */
    public boolean isGlobalSmartQueue() {
        return globalSmartQueue;
    }

    /**
     * True if {@link xyz.dynxsty.dih4jda.SmartQueue} is enabled for global commands.
     *
     * @param globalSmartQueue true if it should be enabled, otherwise false.
     */
    public void setGlobalSmartQueue(boolean globalSmartQueue) {
        this.globalSmartQueue = globalSmartQueue;
    }

    /**
     * True if {@link xyz.dynxsty.dih4jda.SmartQueue} is enabled for guild only commands.<br>
     * <b>Standard:</b> {@code true}
     *
     * @return  true if it is enabled, otherwise false.
     */
    public boolean isGuildSmartQueue() {
        return guildSmartQueue;
    }

    /**
     * True if {@link xyz.dynxsty.dih4jda.SmartQueue} is enabled for guild only commands.
     *
     * @param guildSmartQueue true if it should be enabled, otherwise false.
     */
    public void setGuildSmartQueue(boolean guildSmartQueue) {
        this.guildSmartQueue = guildSmartQueue;
    }

    /**
     * True if unknown commands should be deleted or not.<br>
     * <b>Standard:</b> {@code true}
     *
     * @return true if they are getting deleted, otherwise false.
     */
    public boolean isDeleteUnknownCommands() {
        return deleteUnknownCommands;
    }

    /**
     * True if unknown commands should be deleted and false if not.
     *
     * @param deleteUnknownCommands true if they should be getting deleted, otherwise false.
     */
    public void setDeleteUnknownCommands(boolean deleteUnknownCommands) {
        this.deleteUnknownCommands = deleteUnknownCommands;
    }

    /**
     * True if the stacktrace should be printed on an {@link xyz.dynxsty.dih4jda.events.DIH4JDAThrowableEvent} if no
     * {@link xyz.dynxsty.dih4jda.events.DIH4JDAEventListener} is registered.<br>
     * <b>Standard:</b> {@code true}
     *
     * @return true if they are getting printed.
     */
    public boolean isDefaultPrintStacktrace() {
        return defaultPrintStacktrace;
    }

    /**
     * True if the stacktrace should be printed on an {@link xyz.dynxsty.dih4jda.events.DIH4JDAThrowableEvent} if no
     * {@link xyz.dynxsty.dih4jda.events.DIH4JDAEventListener} is registered.
     *
     * @param defaultPrintStacktrace true if they should be getting printed.
     */
    public void setDefaultPrintStacktrace(boolean defaultPrintStacktrace) {
        this.defaultPrintStacktrace = defaultPrintStacktrace;
    }

    /**
     * Gets you the {@link Executor} that executes every command.<br>
     * <b>Standard:</b> {@code ForkJoinPool.commonPool()}
     *
     * @return the {@link Executor} instance.
     */
    public Executor getExecutor() {
        return executor;
    }

    /**
     * Sets a new {@link Executor}.
     *
     * @param executor the new {@link Executor} to use.
     */
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    /**
     * True if the {@link xyz.dynxsty.dih4jda.exceptions.CommandNotRegisteredException} should be thrown or not. <br>
     * <b>Standard:</b> {@code true}
     *
     * @return true if it will be thrown.
     */
    public boolean isThrowUnregisteredException() {
        return throwUnregisteredException;
    }

    /**
     * True if the {@link xyz.dynxsty.dih4jda.exceptions.CommandNotRegisteredException} should be thrown or not.
     *
     * @param throwUnregisteredException true if it should be thrown.
     */
    public void setThrowUnregisteredException(boolean throwUnregisteredException) {
        this.throwUnregisteredException = throwUnregisteredException;
    }

    // TODO: Docs
    public boolean isEnableHelpCommand() {
        return enableHelpCommand;
    }

    // TODO: Docs
    public void setEnableHelpCommand(boolean enableHelpCommand) {
        this.enableHelpCommand = enableHelpCommand;
    }

    // TODO: Docs
    public void setHelpCommandNames(List<String> helpCommandNames) {
        this.helpCommandNames = helpCommandNames;
    }

    // TODO: Docs
    public List<String> getHelpCommandNames() {
        return helpCommandNames;
    }

    // TODO: Docs
    public void setHelpCommandConsumer(BiConsumer<TextCommandEvent, List<TextCommand>> helpCommandConsumer) {
        this.helpCommandConsumer = helpCommandConsumer;
    }

    // TODO: Docs
    public BiConsumer<TextCommandEvent, List<TextCommand>> getHelpCommandConsumer() {
        return helpCommandConsumer;
    }
}
