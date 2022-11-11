package xyz.dynxsty.dih4jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import xyz.dynxsty.dih4jda.interactions.commands.ContextCommand;
import xyz.dynxsty.dih4jda.interactions.commands.SlashCommand;
import xyz.dynxsty.dih4jda.util.CommandUtils;
import xyz.dynxsty.dih4jda.util.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * <h1>Smart Queue</h1>
 * This Class handles all the SmartQueue functionality which can be disabled using
 * {@link DIH4JDABuilder#setGlobalSmartQueue(boolean)} & {@link DIH4JDABuilder#setGuildSmartQueue(boolean)}.
 * <br><br>
 * <p>
 *     This basically retrieves all existing commands and compares them with the local ones, thus, only queuing
 *     "new" (commands, which yet do not exist) or edited commands , which reduces the amount of total command updates.
 *
 * </p>
 * <br>
 * <p>
 *     In addition to that, this will also delete ALL unknown commands, which were not found locally. This can be disabled
 *     using {@link DIH4JDABuilder#disableUnknownCommandDeletion()}.</p>
 * @since v1.5
 */
public class SmartQueue {
	private final Set<SlashCommand> slashCommands;
	private final Set<ContextCommand> contextCommands;
	private final boolean deleteUnknown;

	protected SmartQueue(@Nonnull Set<SlashCommand> slashCommands, @Nonnull Set<ContextCommand> contextCommands, boolean deleteUnknown) {
		this.slashCommands = slashCommands;
		this.contextCommands = contextCommands;
		this.deleteUnknown = deleteUnknown;
	}

	/**
	 * Compares CommandData with already existing Commands, removed duplicates and, if enabled, deleted unknown commands.
	 *
	 * @param jda The {@link JDA} instance which is used to retrieve the already existing commands.
	 * @return A {@link Pair} with the remaining {@link SlashCommandData} and {@link CommandData}.
	 * @since v1.5
	 */
	protected @Nonnull Pair<Set<SlashCommand>, Set<ContextCommand>> checkGlobal(@Nonnull JDA jda, @Nonnull List<Command> existing) {
		if (!existing.isEmpty()) {
			return removeDuplicates(jda, existing, null);
		}
		return new Pair<>(slashCommands, contextCommands);
	}

	/**
	 * Compares CommandData with already existing Commands, removed duplicates and, if enabled, deletes unknown commands.
	 *
	 * @param guild The {@link Guild} which is used to retrieve the already existing commands.
	 * @return A {@link Pair} with the remaining {@link SlashCommandData} and {@link CommandData}.
	 * @since v1.5
	 */
	protected @Nonnull Pair<Set<SlashCommand>, Set<ContextCommand>> checkGuild(@Nonnull Guild guild, @Nonnull List<Command> existing) {
		if (!existing.isEmpty()) {
			return removeDuplicates(guild.getJDA(), existing, guild);
		}
		return new Pair<>(slashCommands, contextCommands);
	}

	/**
	 * Removes all duplicate {@link CommandData} and, if enabled, deletes unknown commands.
	 *
	 * @param jda      The {@link JDA} instance.
	 * @param existing A List of all existing {@link Command}s.
	 * @param guild    An optional guild parameter which is used with {@link SmartQueue#checkGuild(Guild, List)}.
	 * @return A {@link Pair} with the remaining {@link SlashCommandData} & {@link CommandData}.
	 * @since v1.5
	 */
	private @Nonnull Pair<Set<SlashCommand>, Set<ContextCommand>> removeDuplicates(@Nonnull JDA jda, @Nonnull final List<Command> existing, @Nullable Guild guild) {
		List<Command> commands = new ArrayList<>(existing);
		boolean global = guild == null;
		String prefix = String.format("[%s] ", global ? "Global" : guild.getName());
		DIH4JDALogger.info(DIH4JDALogger.Type.SMART_QUEUE, prefix + "Found %s existing command(s)", existing.size());
		// remove already-existing commands
		commands.removeIf(cmd -> {
			if (contextCommands.stream().anyMatch(data -> CommandUtils.equals(cmd, data.getCommandData(), global)) ||
					slashCommands.stream().anyMatch(data -> CommandUtils.equals(cmd, data.getSlashCommandData(), global))) {
				// check for command in blacklisted guilds
				// this may be refactored soonTM, as its kinda clunky
				if (!global) {
					for (SlashCommand d : slashCommands) {
						if (CommandUtils.equals(cmd, d.getSlashCommandData(), false)) {
							if (d.getRequiredGuilds().getFirst() == null) {
								return true;
							} else {
								if (!Arrays.asList(d.getRequiredGuilds().getSecond()).contains(guild.getIdLong())) {
									DIH4JDALogger.info("Deleting /%s in Guild: %s", cmd.getName(), guild.getName());
									cmd.delete().queue();
									return true;
								}
							}
						}
					}
					for (ContextCommand d : contextCommands) {
						if (CommandUtils.equals(cmd, d.getCommandData(), false) &&
								!Arrays.asList(d.getRequiredGuilds().getSecond()).contains(guild.getIdLong())) {
							DIH4JDALogger.info("Deleting %s in Guild: %s", cmd.getName(), guild.getName());
							cmd.delete().queue();
							return true;
						}
					}
				}
				DIH4JDALogger.info(DIH4JDALogger.Type.SMART_QUEUE_IGNORED, prefix + "Found duplicate %s command, which will be ignored: %s", cmd.getType(), cmd.getName());
				return true;
			}
			return false;
		});
		contextCommands.removeIf(data -> existing.stream().anyMatch(p -> CommandUtils.equals(p, data.getCommandData(), global)));
		slashCommands.removeIf(data -> existing.stream().anyMatch(p -> CommandUtils.equals(p, data.getSlashCommandData(), global)));
		// remove unknown commands, if enabled
		if (!commands.isEmpty()) {
			for (Command command : commands) {
				if (existing.contains(command)) {
					if (deleteUnknown) {
						DIH4JDALogger.info(DIH4JDALogger.Type.SMART_QUEUE_DELETED_UNKNOWN, prefix + "Deleting unknown %s command: %s", command.getType(), command.getName());
						if (global) {
							jda.deleteCommandById(command.getId()).queue();
						} else {
							guild.deleteCommandById(command.getId()).queue();
						}
					} else {
						DIH4JDALogger.info(DIH4JDALogger.Type.SMART_QUEUE_IGNORED_UNKNOWN, prefix + "Ignored unknown %s command: %s", command.getType(), command.getName());
					}
				}
			}
		}
		return new Pair<>(slashCommands, contextCommands);
	}
}
