package com.dynxsty.dih4jda.interactions.commands.model;

import com.dynxsty.dih4jda.interactions.commands.RegistrationType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.HashSet;
import java.util.Set;

/**
 * Model class which holds the {@link CommandData} and it's {@link RegistrationType}.
 */
public class UnqueuedCommandData {
	private final CommandData data;
	private final RegistrationType type;
	private Set<Guild> guilds;

	public UnqueuedCommandData(CommandData data, RegistrationType type) {
		this.data = data;
		this.type = type;
		this.guilds = new HashSet<>();
	}

	public CommandData getData() {
		return data;
	}

	public RegistrationType getType() {
		return type;
	}

	public Set<Guild> getGuilds() {
		return guilds;
	}

	public void setGuilds(Set<Guild> guilds) {
		this.guilds = guilds;
	}
}
