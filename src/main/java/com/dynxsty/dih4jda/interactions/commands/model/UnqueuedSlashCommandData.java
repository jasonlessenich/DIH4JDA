package com.dynxsty.dih4jda.interactions.commands.model;

import com.dynxsty.dih4jda.interactions.commands.ExecutableCommand;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Model class which holds the {@link SlashCommandData} and it's {@link ExecutableCommand.Type}.
 */
public class UnqueuedSlashCommandData {
	private final SlashCommandData data;
	private final ExecutableCommand.Type type;

	public UnqueuedSlashCommandData(SlashCommandData data, ExecutableCommand.Type type) {
		this.data = data;
		this.type = type;
	}

	public SlashCommandData getData() {
		return data;
	}

	public ExecutableCommand.Type getType() {
		return type;
	}
}
