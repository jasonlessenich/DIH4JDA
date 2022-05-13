package com.dynxsty.dih4jda.interactions.commands.model;

import com.dynxsty.dih4jda.interactions.commands.ExecutableCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * Model class which holds the {@link CommandData} and it's {@link ExecutableCommand.Type}.
 */
public class UnqueuedCommandData {
	private final CommandData data;
	private final ExecutableCommand.Type type;

	public UnqueuedCommandData(CommandData data, ExecutableCommand.Type type) {
		this.data = data;
		this.type = type;
	}

	public CommandData getData() {
		return data;
	}

	public ExecutableCommand.Type getType() {
		return type;
	}
}
