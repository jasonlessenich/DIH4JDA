package xyz.dynxsty.dih4jda.config;

import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.DIH4JDALogger;
import xyz.dynxsty.dih4jda.events.interactions.TextCommandEvent;
import xyz.dynxsty.dih4jda.interactions.commands.text.TextCommand;
import xyz.dynxsty.dih4jda.util.CommandUtils;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

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
     */
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
     * <b>Standard:</b> {@link ForkJoinPool#commonPool()}
     */
    private Executor executor = ForkJoinPool.commonPool();

    /**
     * Whether the help command is enabled.
     */
    private boolean enableHelpCommand = true;

    /**
     * A list of names that trigger the help list.
     */
    private List<String> helpCommandNames = List.of("help");

    /**
     * The {@link BiConsumer} used to generate the help list.
     */
    private BiConsumer<TextCommandEvent, List<TextCommand>> helpCommandConsumer = (event, commands) -> {
        final String prefix = event.getDIH4JDA().getEffectivePrefix(event.getGuild());
        final Map<String, List<TextCommand>> categorizedCommand = event.getDIH4JDA().getTextCommandsCategorized("Uncategorized");
        // build embed
        final EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Help List")
                .setColor(Color.blue)
                .setTimestamp(Instant.now());
        categorizedCommand.forEach((category, list) ->
                builder.addField(category, list.stream().map(c ->
                        CommandUtils.formatTextCommand(prefix, c)).collect(Collectors.joining("\n")), false));
        event.getMessage().replyEmbeds(builder.build()).queue();
    };

    /**
     * Creates a default instance.
     */
    public DIH4JDAConfig() {}
}
