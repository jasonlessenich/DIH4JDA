package com.dynxsty.dih4jda;

import com.dynxsty.dih4jda.config.DIH4JDAConfig;
import com.dynxsty.dih4jda.exceptions.DIH4JDAException;
import com.dynxsty.dih4jda.exceptions.InvalidPackageException;
import com.dynxsty.dih4jda.interactions.commands.ExecutableCommand;
import net.dv8tion.jda.api.JDA;
import org.reflections.util.ClasspathHelper;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
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
        config.setCommandsPackage(pack);
        return this;
    }

    /**
     * Sets the Executor that will be used to execute all commands.
     *
     * @param executor The Executor.
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
     */
    @Nonnull
    public DIH4JDABuilder disableLogging(DIH4JDALogger.Type... types) {
        DIH4JDALogger.Type[] blocked;
        if (types == null || types.length < 1) {
            blocked = DIH4JDALogger.Type.values();
        } else {
            blocked = types;
        }
        config.setBlockedLogTypes(Arrays.stream(blocked).collect(Collectors.toSet()));
        return this;
    }

    /**
     * Whether DIH4JDA should automatically register all interactions on each onReady event.
     * A manual registration of all interactions can be executed using {@link DIH4JDA#registerInteractions()}.
     */
    @Nonnull
    public DIH4JDABuilder disableAutomaticCommandRegistration() {
        config.setRegisterOnReady(false);
        return this;
    }

    /**
     * <b>NOT RECOMMENDED</b> (unless there are some bugs) <br>
     * This will disable the Smart Queueing functionality.
     * If SmartQueue is disabled Global Slash/Context Commands get overridden on each {@link DIH4JDA#registerInteractions()} call,
     * thus, making Global Commands unusable for about an hour, until they're registered again. <br>
     * By default, this also deletes unknown/unused commands. This behaviour can be disabled with {@link DIH4JDABuilder#disableUnknownCommandDeletion()}.
     */
    @Nonnull
    public DIH4JDABuilder disableSmartQueue() {
        config.setSmartQueuing(false);
        return this;
    }

    /**
     * Sets the default {@link ExecutableCommand.Type} for all Commands.
     *
     * @param type The {@link ExecutableCommand.Type}.
     */
    public DIH4JDABuilder setDefaultCommandType(ExecutableCommand.Type type) {
        DIH4JDA.defaultCommandType = type;
        return this;
    }

    /**
     * Disables deletion of unknown/unused commands when using SmartQueue.
     */
    @Nonnull
    public DIH4JDABuilder disableUnknownCommandDeletion() {
        config.setDeleteUnknownCommands(false);
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
        if (ClasspathHelper.forPackage(config.getCommandsPackage()).isEmpty()) {
            throw new InvalidPackageException("Package " + config.getCommandsPackage() + " does not exist.");
        }
        config.setJDA(jda);
        return new DIH4JDA(config);
    }
}
