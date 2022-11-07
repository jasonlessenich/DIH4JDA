package xyz.dynxsty.dih4jda.interactions.commands.model;

import xyz.dynxsty.dih4jda.interactions.commands.RegistrationType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.HashSet;
import java.util.Set;

/**
 * Model class which holds the {@link SlashCommandData} and it's {@link RegistrationType}.
 */
public class UnqueuedSlashCommandData {
	private final SlashCommandData data;
	private final RegistrationType type;
	private Set<Guild> guilds;

	public UnqueuedSlashCommandData(SlashCommandData data, RegistrationType type) {
		this.data = data;
		this.type = type;
		this.guilds = new HashSet<>();
	}

	public SlashCommandData getData() {
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
