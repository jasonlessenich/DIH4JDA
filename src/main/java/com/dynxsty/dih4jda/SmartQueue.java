package com.dynxsty.dih4jda;

import com.dynxsty.dih4jda.util.CommandUtils;
import kotlin.Pair;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Utility class that contains some useful methods regarding the SmartQueue functionality.
 * @since v1.5
 */
public class SmartQueue {
	private SmartQueue() {
	}

	/**
	 * Compares CommandData with already existing Commands, removed duplicates and deletes unknown Global Commands.
	 *
	 * @param jda         The {@link JDA} instance which is used to retrieve the already existing commands.
	 * @param slashData   The set of {@link SlashCommandData}.
	 * @param commandData The set of {@link CommandData}.
	 * @return A {@link Pair} with the remaining {@link SlashCommandData} & {@link CommandData}.
	 * @since v1.5
	 */
	protected static Pair<Set<SlashCommandData>, Set<CommandData>> checkGlobal(JDA jda, Set<SlashCommandData> slashData, Set<CommandData> commandData) {
		final List<Command> existing = jda.retrieveCommands().complete();
		if (!existing.isEmpty()) {
			return removeDuplicates(jda, existing, slashData, commandData, null);
		}
		return new Pair<>(slashData, commandData);
	}

	/**
	 * Compares CommandData with already existing Commands, removed duplicates and deletes unknown Guild Commands.
	 *
	 * @param guild       The {@link Guild} which is used to retrieve the already existing commands.
	 * @param slashData   The set of {@link SlashCommandData}.
	 * @param commandData The set of {@link CommandData}.
	 * @return A {@link Pair} with the remaining {@link SlashCommandData} & {@link CommandData}.
	 * @since v1.5
	 */
	protected static Pair<Set<SlashCommandData>, Set<CommandData>> checkGuild(Guild guild, Set<SlashCommandData> slashData, Set<CommandData> commandData) {
		final List<Command> existing = guild.retrieveCommands().complete();
		if (!existing.isEmpty()) {
			return removeDuplicates(guild.getJDA(), existing, slashData, commandData, guild);
		}
		return new Pair<>(slashData, commandData);
	}

	/**
	 * Removes all duplicate CommandData and deletes unknown commands.
	 *
	 * @param jda         The {@link JDA} instance.
	 * @param existing    A List of all existing {@link Command}s.
	 * @param slashData   The set of {@link SlashCommandData}.
	 * @param commandData The set of {@link CommandData}.
	 * @param guild       An optional guild parameter which is used with {@link SmartQueue#checkGuild(Guild, Set, Set)}.
	 * @return A {@link Pair} with the remaining {@link SlashCommandData} & {@link CommandData}.
	 * @since v1.5
	 */
	private static Pair<Set<SlashCommandData>, Set<CommandData>> removeDuplicates(JDA jda, final List<Command> existing, Set<SlashCommandData> slashData, Set<CommandData> commandData, @Nullable Guild guild) {
		List<Command> commands = new ArrayList<>(existing);
		boolean global = guild == null;
		DIH4JDALogger.info(String.format("Found %s existing %s command(s)", existing.size(), global ? "global" : "guild"), DIH4JDALogger.Type.SMART_QUEUE);
		// remove already-existing commands
		commands.removeIf(cmd -> {
			if (commandData.stream().anyMatch(data -> CommandUtils.isEqual(cmd, data)) ||
					slashData.stream().anyMatch(data -> CommandUtils.isEqual(cmd, data))) {
				DIH4JDALogger.info(String.format("Found duplicate %s command, which will be ignored: %s", cmd.getType(), cmd.getName()), DIH4JDALogger.Type.SMART_QUEUE);
				return true;
			}
			return false;
		});
		commandData.removeIf(data -> existing.stream().anyMatch(p -> CommandUtils.isEqual(p, data)));
		slashData.removeIf(data -> existing.stream().anyMatch(p -> CommandUtils.isEqual(p, data)));
		// remove unknown commands
		if (!commands.isEmpty()) {
			for (Command command : commands) {
				if (existing.contains(command)) {
					DIH4JDALogger.info(String.format("Deleting unknown %s command: %s", command.getType(), command.getName()), DIH4JDALogger.Type.SMART_QUEUE);
					if (guild == null) {
						jda.deleteCommandById(command.getId()).queue();
					} else {
						guild.deleteCommandById(command.getId()).queue();
					}
				}
			}
		}
		return new Pair<>(slashData, commandData);
	}
}
