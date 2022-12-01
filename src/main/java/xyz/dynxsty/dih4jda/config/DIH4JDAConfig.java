package xyz.dynxsty.dih4jda.config;

import lombok.Data;
import net.dv8tion.jda.api.JDA;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.DIH4JDALogger;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * Simple data class which represents {@link DIH4JDA}'s configuration.
 */
@Data
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
    private Executor executor = ForkJoinPool.commonPool();

    /**
     * Creates a default instance.
     */
    public DIH4JDAConfig() {}
}
