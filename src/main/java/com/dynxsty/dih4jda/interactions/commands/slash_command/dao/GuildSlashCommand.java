package com.dynxsty.dih4jda.interactions.commands.slash_command.dao;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.util.*;
import java.util.stream.Collectors;

public abstract class GuildSlashCommand extends BaseSlashCommand {
	private Set<Guild> whitelistedGuilds = new HashSet<>();

	private Set<Guild> blacklistedGuilds = new HashSet<>();;

	/**
	 * Allows a set of {@link Guild}s to update their Slash Commands.
	 *
	 * @param jda The {@link JDA} instance.
	 * @param whitelisted An array of {@link Long}s.
	 */
	public void whitelistGuilds(JDA jda, Long... whitelisted) {
		this.whitelistedGuilds = Arrays.stream(whitelisted).map(jda::getGuildById).collect(Collectors.toSet());
	}

	/**
	 * Prevents the given set of {@link Guild}s from updating their Slash Commands.
	 *
	 * @param jda The {@link JDA} instance.
	 * @param blacklisted An array of {@link Long}s.
	 */
	public void blacklistGuilds(JDA jda, Long... blacklisted) {
		this.blacklistedGuilds = Arrays.stream(blacklisted).map(jda::getGuildById).collect(Collectors.toSet());
	}

	/**
	 * Allows a set of {@link Guild}s to update their Slash Commands.
	 *
	 * @param whitelisted An array of {@link Guild}s.
	 */
	public void whitelistGuilds(Guild... whitelisted) {
		this.whitelistedGuilds = Arrays.stream(whitelisted).collect(Collectors.toSet());
	}

	/**
	 * Prevents the given set of {@link Guild}s from updating their Slash Commands.
	 *
	 * @param blacklisted An array of {@link Guild}s.
	 */
	public void blacklistGuilds(Guild... blacklisted) {
		this.blacklistedGuilds = Arrays.stream(blacklisted).collect(Collectors.toSet());
	}

	/**
	 * Gets all Guilds whose Slash Commands should be updated.
	 *
	 * @param jda The {@link JDA} instance.
	 * @return A {@link List} with all Guilds.
	 */
	public Set<Guild> getGuilds(JDA jda) {
		Set<Guild> guilds = new HashSet<>(jda.getGuilds());
		guilds.removeIf(g -> this.blacklistedGuilds.contains(g));
		if (whitelistedGuilds.size() > 0) {
			guilds = this.whitelistedGuilds;
		}
		return guilds;
	}
}
