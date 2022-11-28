package xyz.dynxsty.dih4jda;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import xyz.dynxsty.dih4jda.interactions.commands.application.ContextCommand;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;
import xyz.dynxsty.dih4jda.util.CommandUtils;
import xyz.dynxsty.dih4jda.util.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * <b>Smart Queue</b><br>
 * This Class handles all the SmartQueue functionality which can be disabled using
 * {@link DIH4JDABuilder#setGlobalSmartQueue(boolean)} and {@link DIH4JDABuilder#setGuildSmartQueue(boolean)}.
 * <br><br>
 * <p>
 * This basically retrieves all existing commands and compares them with the local ones, thus, only queuing
 * "new" (commands, which yet do not exist) or edited commands, which reduces the amount of total command updates.
 *
 * </p>
 * <br>
 * <p>
 * In addition to that, this will also delete ALL unknown commands, which were not found locally. This can be disabled
 * using {@link DIH4JDABuilder#disableUnknownCommandDeletion()}.</p>
 *
 * @since v1.5
 */
public class SmartQueue {
	private final Set<SlashCommand> slashCommands;
	private final Set<ContextCommand<?>> contextCommands;
	private final boolean deleteUnknown;

	protected SmartQueue(@Nonnull Set<SlashCommand> slashCommands, @Nonnull Set<ContextCommand<?>> contextCommands,
						 boolean deleteUnknown) {
		this.slashCommands = slashCommands;
		this.contextCommands = contextCommands;
		this.deleteUnknown = deleteUnknown;
	}

	/**
	 * Compares the specified {@link CommandData} with already existing Commands, removes duplicates and, if enabled, deletes unknown commands.
	 *
	 * @return A {@link Pair} with the remaining {@link SlashCommandData} and {@link CommandData}.
	 * @since v1.5
	 */
	@Nonnull
	protected Pair<Set<SlashCommand>, Set<ContextCommand<?>>> checkGlobal(@Nonnull List<Command> existing) {
		if (!existing.isEmpty()) {
			return removeDuplicates(existing, null);
		}
		return new Pair<>(slashCommands, contextCommands);
	}

	/**
	 * Compares the specified {@link CommandData} with already existing Commands, removes duplicates and, if enabled, deletes unknown commands.
	 *
	 * @param guild The {@link Guild} which is used to retrieve the already existing commands.
	 * @return A {@link Pair} with the remaining {@link SlashCommandData} and {@link CommandData}.
	 * @since v1.5
	 */
	@Nonnull
	protected Pair<Set<SlashCommand>, Set<ContextCommand<?>>> checkGuild(@Nonnull Guild guild, @Nonnull List<Command> existing) {
		if (!existing.isEmpty()) {
			return removeDuplicates(existing, guild);
		}
		return new Pair<>(slashCommands, contextCommands);
	}

	/**
	 * Removes all duplicate {@link CommandData} and, if enabled, deletes unknown commands.
	 *
	 * @param existing A List of all existing {@link Command}s.
	 * @param guild    An optional guild parameter which is used with {@link SmartQueue#checkGuild(Guild, List)}.
	 * @return A {@link Pair} with the remaining {@link SlashCommandData} & {@link CommandData}.
	 * @since v1.5
	 */
	@Nonnull
	private Pair<Set<SlashCommand>, Set<ContextCommand<?>>> removeDuplicates(@Nonnull final List<Command> existing, @Nullable Guild guild) {
		List<Command> commands = new ArrayList<>(existing);
		boolean global = guild == null;
		String prefix = String.format("[%s] ", global ? "Global" : guild.getName());
		DIH4JDALogger.info(DIH4JDALogger.Type.SMART_QUEUE, prefix + "Found %s existing command(s)", existing.size());
		// remove already-existing commands
		commands.removeIf(cmd -> {
			boolean isCheckingGuilds;
			if (cmd.getType().equals(Command.Type.SLASH)) {
				isCheckingGuilds = slashCommands.stream().anyMatch(data -> CommandUtils.compareSlashCommands(cmd, data));
			} else {
				isCheckingGuilds = contextCommands.stream().anyMatch(data -> CommandUtils.compareContextCommands(cmd, data));
			}
			if (isCheckingGuilds) {
				// check for command in blacklisted guilds
				if (!global) {
					slashCommands.forEach(slash -> checkRequiredGuilds(guild, cmd, slash));
					contextCommands.forEach(context -> checkRequiredGuilds(guild, cmd, context));
				} else {
					DIH4JDALogger.info(DIH4JDALogger.Type.SMART_QUEUE_IGNORED, prefix + "Found duplicate %s command, which will be ignored: %s", cmd.getType(), cmd.getName());
				}
				return true;
			}
			return false;
		});
		contextCommands.removeIf(data -> existing.stream().anyMatch(cmd -> CommandUtils.compareContextCommands(cmd, data)));
		slashCommands.removeIf(data -> existing.stream().anyMatch(cmd -> CommandUtils.compareSlashCommands(cmd, data)));
		// remove unknown commands, if enabled
		if (!commands.isEmpty()) {
			commands.forEach(cmd -> checkUnknown(prefix, existing, cmd));
		}
		return new Pair<>(slashCommands, contextCommands);
	}

	private void checkUnknown(@Nonnull String prefix, @Nonnull final List<Command> existing, @Nonnull Command command) {
		if (existing.contains(command)) {
			if (deleteUnknown) {
				DIH4JDALogger.info(DIH4JDALogger.Type.SMART_QUEUE_DELETED_UNKNOWN, prefix + "Deleting unknown %s command: %s", command.getType(), command.getName());
				command.delete().queue();
			} else {
				DIH4JDALogger.info(DIH4JDALogger.Type.SMART_QUEUE_IGNORED_UNKNOWN, prefix + "Ignored unknown %s command: %s", command.getType(), command.getName());
			}
		}
	}

	private void checkRequiredGuilds(Guild guild, Command cmd, @Nonnull SlashCommand data) {
		if (CommandUtils.compareSlashCommands(cmd, data) && data.getQueueableGuilds().length != 0 &&
				!Arrays.asList(data.getQueueableGuilds()).contains(guild.getIdLong())) {
			DIH4JDALogger.info("Deleting /%s in non-queueable Guild: %s", cmd.getName(), guild.getName());
			cmd.delete().queue();
		}
	}

	private void checkRequiredGuilds(Guild guild, Command cmd, @Nonnull ContextCommand<?> data) {
		if (CommandUtils.compareContextCommands(cmd, data) && data.getQueueableGuilds().length != 0 &&
				!Arrays.asList(data.getQueueableGuilds()).contains(guild.getIdLong())) {
			DIH4JDALogger.info("Deleting /%s in non-queueable Guild: %s", cmd.getName(), guild.getName());
			cmd.delete().queue();
		}
	}
}
