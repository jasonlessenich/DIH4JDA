package com.dynxsty.dih4jda;

import com.dynxsty.dih4jda.util.CommandUtils;
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
public class SmartQueue {

	// TODO v1.5: Documentation
	protected static void queueGlobal(JDA jda, Set<SlashCommandData> slashData, Set<CommandData> commandData) {
		final List<Command> existingData = jda.retrieveCommands().complete();
		removeDuplicates(jda, existingData, slashData, commandData, null);
	}

	// TODO v1.5: Documentation
	protected static void queueGuild(Guild guild, Set<SlashCommandData> slashData, Set<CommandData> commandData) {
		final List<Command> existingData = guild.retrieveCommands().complete();
		removeDuplicates(guild.getJDA(), existingData, slashData, commandData, guild);
	}

	// TODO v1.5: Documentation
	private static void removeDuplicates(JDA jda, List<Command> existingData, Set<SlashCommandData> slashData, Set<CommandData> commandData, @Nullable Guild guild) {
		List<Command> queue = new ArrayList<>(existingData);
		DIH4JDALogger.info(String.format("Found %s existing %s command(s). Trying to just queue edited commands...",
				existingData.size(), guild == null ? "global" : "guild"), DIH4JDALogger.Type.SMART_QUEUE);
		commandData.removeIf(command -> existingData.stream().anyMatch(data -> CommandUtils.isCommandData(queue, data, command)));
		slashData.removeIf(command -> existingData.stream().anyMatch(data -> CommandUtils.isCommandData(queue, data, command)));
		// remove unknown commands
		if (!queue.isEmpty()) {
			DIH4JDALogger.info(String.format("Found %s unknown command(s). Attempting deletion.", queue.size()), DIH4JDALogger.Type.SMART_QUEUE);
			for (Command command : queue) {
				DIH4JDALogger.info(String.format("Deleting unknown %s command: %s", command.getType(), command.getName()), DIH4JDALogger.Type.SMART_QUEUE);
				if (guild == null) {
					jda.deleteCommandById(command.getId()).queue();
				} else {
					guild.deleteCommandById(command.getId()).queue();
				}
			}
		}
	}
}
