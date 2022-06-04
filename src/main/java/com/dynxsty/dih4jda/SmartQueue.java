package com.dynxsty.dih4jda;

import com.dynxsty.dih4jda.interactions.commands.model.UnqueuedCommandData;
import com.dynxsty.dih4jda.interactions.commands.model.UnqueuedSlashCommandData;
import com.dynxsty.dih4jda.util.CommandUtils;
import com.dynxsty.dih4jda.util.Pair;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Utility class that contains some useful methods regarding the SmartQueue functionality.
 *
 * @since v1.5
 */
public class SmartQueue {
	private SmartQueue() {
	}

	/**
	 * Compares CommandData with already existing Commands, removed duplicates and, if enabled, deleted unknown commands.
	 *
	 * @param jda         The {@link JDA} instance which is used to retrieve the already existing commands.
	 * @param slashData   The set of {@link SlashCommandData}.
	 * @param commandData The set of {@link CommandData}.
	 * @return A {@link Pair} with the remaining {@link SlashCommandData} & {@link CommandData}.
	 * @since v1.5
	 */
	protected static Pair<Set<UnqueuedSlashCommandData>, Set<UnqueuedCommandData>> checkGlobal(JDA jda, Set<UnqueuedSlashCommandData> slashData, Set<UnqueuedCommandData> commandData, boolean deleteUnknown) {
		List<Command> existing;
		try {
			existing = jda.retrieveCommands().complete();
		} catch (ErrorResponseException e) {
			DIH4JDALogger.error("Could not retrieve Global Commands! Please make sure that the bot was invited with " +
					"the application.commands scope!");
			return new Pair<>(Set.of(), Set.of());
		}
		if (!existing.isEmpty()) {
			return removeDuplicates(jda, existing, slashData, commandData, null, deleteUnknown);
		}
		return new Pair<>(slashData, commandData);
	}

	/**
	 * Compares CommandData with already existing Commands, removed duplicates and, if enabled, deletes unknown commands.
	 *
	 * @param guild       The {@link Guild} which is used to retrieve the already existing commands.
	 * @param slashData   The set of {@link SlashCommandData}.
	 * @param commandData The set of {@link CommandData}.
	 * @return A {@link Pair} with the remaining {@link SlashCommandData} & {@link CommandData}.
	 * @since v1.5
	 */
	protected static Pair<Set<UnqueuedSlashCommandData>, Set<UnqueuedCommandData>> checkGuild(Guild guild, Set<UnqueuedSlashCommandData> slashData, Set<UnqueuedCommandData> commandData, boolean deleteUnknown) {
		List<Command> existing;
		try {
			existing = guild.retrieveCommands().complete();
		} catch (ErrorResponseException e) {
			DIH4JDALogger.error("Could not retrieve Commands from Guild " + guild.getName() + "!" +
					" Please make sure that the bot was invited with the application.commands scope!");
			return new Pair<>(Set.of(), Set.of());
		}
		if (!existing.isEmpty()) {
			return removeDuplicates(guild.getJDA(), existing, slashData, commandData, guild, deleteUnknown);
		}
		return new Pair<>(slashData, commandData);
	}

	/**
	 * Removes all duplicate CommandData and, if enabled, deletes unknown commands.
	 *
	 * @param jda           The {@link JDA} instance.
	 * @param existing      A List of all existing {@link Command}s.
	 * @param slashData     The set of {@link SlashCommandData}.
	 * @param commandData   The set of {@link CommandData}.
	 * @param guild         An optional guild parameter which is used with {@link SmartQueue#checkGuild(Guild, Set, Set, boolean)}.
	 * @param deleteUnknown Whether unknown commands should be removed.
	 * @return A {@link Pair} with the remaining {@link SlashCommandData} & {@link CommandData}.
	 * @since v1.5
	 */
	private static Pair<Set<UnqueuedSlashCommandData>, Set<UnqueuedCommandData>> removeDuplicates(JDA jda, final List<Command> existing, Set<UnqueuedSlashCommandData> slashData, Set<UnqueuedCommandData> commandData, @Nullable Guild guild, boolean deleteUnknown) {
		List<Command> commands = new ArrayList<>(existing);
		boolean global = guild == null;
		String prefix = String.format("[%s] ", global ? "Global" : guild.getName());
		DIH4JDALogger.info(String.format(prefix + "Found %s existing command(s)", existing.size()), DIH4JDALogger.Type.SMART_QUEUE);
		// remove already-existing commands
		commands.removeIf(cmd -> {
			if (commandData.stream().anyMatch(data -> CommandUtils.equals(CommandData.fromCommand(cmd), data.getData())) ||
					slashData.stream().anyMatch(data -> CommandUtils.equals(SlashCommandData.fromCommand(cmd), data.getData()))) {
				// check for command in blacklisted guilds
				// this may be refactored soonTM, as its kinda clunky
				if (guild != null) {
					for (UnqueuedSlashCommandData d : slashData) {
						if (CommandUtils.equals(SlashCommandData.fromCommand(cmd), d.getData()) && !d.getGuilds().contains(guild)) {
							DIH4JDALogger.info("Deleting /" + cmd.getName() + " in Guild: " + guild.getName());
							cmd.delete().queue();
							return true;
						}
					}
					for (UnqueuedCommandData d : commandData) {
						if (CommandUtils.equals(CommandData.fromCommand(cmd), d.getData()) && !d.getGuilds().contains(guild)) {
							DIH4JDALogger.info("Deleting " + cmd.getName() + " in Guild: " + guild.getName());
							cmd.delete().queue();
							return true;
						}
					}
				}
				DIH4JDALogger.info(String.format(prefix + "Found duplicate %s command, which will be ignored: %s", cmd.getType(), cmd.getName()), DIH4JDALogger.Type.SMART_QUEUE);
				return true;
			}
			return false;
		});
		commandData.removeIf(data -> existing.stream().anyMatch(p -> CommandUtils.equals(CommandData.fromCommand(p), data.getData())));
		slashData.removeIf(data -> existing.stream().anyMatch(p -> CommandUtils.equals(SlashCommandData.fromCommand(p), data.getData())));
		// remove unknown commands, if enabled
		if (!commands.isEmpty()) {
			for (Command command : commands) {
				if (existing.contains(command)) {
					if (deleteUnknown) {
						DIH4JDALogger.info(String.format(prefix + "Deleting unknown %s command: %s", command.getType(), command.getName()), DIH4JDALogger.Type.SMART_QUEUE);
						if (guild == null) {
							jda.deleteCommandById(command.getId()).queue();
						} else {
							guild.deleteCommandById(command.getId()).queue();
						}
					} else {
						DIH4JDALogger.info(String.format(prefix + "Ignored unknown %s command: %s", command.getType(), command.getName()), DIH4JDALogger.Type.SMART_QUEUE);
					}
				}
			}
		}
		return new Pair<>(slashData, commandData);
	}
}
