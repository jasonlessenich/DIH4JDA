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

// TODO v1.5: Documentation
// FIXME: 06.05.2022
public class SmartQueue {

	// TODO v1.5: Documentation
	protected static Pair<Set<SlashCommandData>, Set<CommandData>> queueGlobal(JDA jda, Set<SlashCommandData> slashData, Set<CommandData> commandData) {
		final List<Command> existing = jda.retrieveCommands().complete();
		if (!existing.isEmpty()) {
			return removeDuplicates(jda, existing, slashData, commandData, null);
		}
		return new Pair<>(slashData, commandData);
	}

	// TODO v1.5: Documentation
	protected static Pair<Set<SlashCommandData>, Set<CommandData>> queueGuild(Guild guild, Set<SlashCommandData> slashData, Set<CommandData> commandData) {
		final List<Command> existing = guild.retrieveCommands().complete();
		if (!existing.isEmpty()) {
			return removeDuplicates(guild.getJDA(), existing, slashData, commandData, guild);
		}
		return new Pair<>(slashData, commandData);
	}

	// TODO v1.5: Documentation
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
					if (guild == null) jda.deleteCommandById(command.getId()).queue();
					else guild.deleteCommandById(command.getId()).queue();
				}
			}
		}
		return new Pair<>(slashData, commandData);
	}
}
