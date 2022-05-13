package com.dynxsty.dih4jda.interactions.commands.model;

import com.dynxsty.dih4jda.interactions.commands.ExecutableCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.HashSet;
import java.util.Set;

/**
 * Model class which holds the {@link CommandData} and it's {@link ExecutableCommand.Type}.
 */
public class UnqueuedCommandData {
	private final CommandData data;
	private final ExecutableCommand.Type type;
	private Set<Guild> guilds;

	public UnqueuedCommandData(CommandData data, ExecutableCommand.Type type) {
		this.data = data;
		this.type = type;
		this.guilds = new HashSet<>();
	}

	public CommandData getData() {
		return data;
	}

	public ExecutableCommand.Type getType() {
		return type;
	}

	public Set<Guild> getGuilds() {
		return guilds;
	}

	public void setGuilds(Set<Guild> guilds) {
		this.guilds = guilds;
	}
}
