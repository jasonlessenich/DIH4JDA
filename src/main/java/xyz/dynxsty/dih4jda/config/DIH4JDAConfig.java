package xyz.dynxsty.dih4jda.config;

import lombok.Data;
import net.dv8tion.jda.api.JDA;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.DIH4JDALogger;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/**
 * Simple data class which represents {@link DIH4JDA}'s configuration.
 */
@Data
public class DIH4JDAConfig {
    /**
     * The {@link JDA} instance that {@link DIH4JDA} uses.
     */
    private JDA jda;

    /**
     * The packages where the commands are located in.<br>
     * <b>Standard:</b> {@code new String[]{}}
     */
    private String[] commandsPackages = new String[]{};

    /**
     * All {@link DIH4JDALogger.Type}s that are not getting logged.<br>
     * <b>Standard:</b> {@code new DIH4JDALogger.Type[]{}}
     * @deprecated Use {@link DIH4JDALogger#disableLogging(DIH4JDALogger.Type...)} <b>Will be removed in 2.0</b>
     */
    @Deprecated(forRemoval = true)
    private DIH4JDALogger.Type[] blockedLogTypes = new DIH4JDALogger.Type[]{};

    /**
     * A boolean that decides if commands are getting registered on the
     * {@link net.dv8tion.jda.api.events.session.ReadyEvent}.<br>
     * <b>Standard:</b> {@code true}
     */

    private boolean registerOnReady = true;

    /**
     * A boolean that decides if {@link xyz.dynxsty.dih4jda.SmartQueue} should be enabled for the
     * {@link xyz.dynxsty.dih4jda.interactions.commands.application.RegistrationType#GLOBAL}.<br>
     * <b>Standard:</b> {@code true}
     */
    private boolean globalSmartQueue = true;

    /**
     * A boolean that decides if {@link xyz.dynxsty.dih4jda.SmartQueue} should be enabled for the
     * {@link xyz.dynxsty.dih4jda.interactions.commands.application.RegistrationType#GUILD}.<br>
     * <b>Standard:</b> {@code true}
     */
    private boolean guildSmartQueue = true;

    /**
     * A boolean that decides if unknown commands should be deleted or not.<br>
     * <b>Standard:</b> {@code true}
     */
    private boolean deleteUnknownCommands = true;

    /**
     * A boolean that decides if a {@link xyz.dynxsty.dih4jda.exceptions.CommandNotRegisteredException} should be
     * thrown or not.<br>
     * <b>Standard:</b> {@code true}
     */
    private boolean throwUnregisteredException = true;

    /**
     * A boolean that decides if the stacktrace should be printed on an
     * {@link xyz.dynxsty.dih4jda.events.DIH4JDAThrowableEvent} if no
     * {@link xyz.dynxsty.dih4jda.events.DIH4JDAEventListener} is registered.
     * <b>Standard:</b> {@code true}
     */
    private boolean defaultPrintStacktrace = true;

    /**
     * The {@link Executor} that will be used to handle the executions of commands.<br>
     * If you have access to Java 21 it's recommended to use the {@link Executors#newVirtualThreadPerTaskExecutor()}.<br>
     * <b>Standard:</b> {@link ForkJoinPool#commonPool()}
     */
    private Executor executor = ForkJoinPool.commonPool();

    /**
     * Creates a default instance.
     */
    public DIH4JDAConfig() {}
}
