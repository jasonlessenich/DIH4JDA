package com.dynxsty.dih4jda.interactions.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// TODO v1.5: Documentation
public abstract class GuildInteraction extends ComponentHandler {

	private boolean isGuildCommand = true;

	private Set<Guild> whitelistedGuilds = new HashSet<>();

	private Set<Guild> blacklistedGuilds = new HashSet<>();

	private Set<Permission> requiredPermissions = new HashSet<>();

	/**
	 * Allows a set of {@link Guild}s to update their Slash Commands.
	 *
	 * @param jda         The {@link JDA} instance.
	 * @param whitelisted An array of {@link Long}s.
	 */
	public void whitelistGuilds(JDA jda, Long... whitelisted) {
		whitelistedGuilds = Arrays.stream(whitelisted).map(jda::getGuildById).collect(Collectors.toSet());
	}

	/**
	 * Prevents the given set of {@link Guild}s from updating their Slash Commands.
	 *
	 * @param jda         The {@link JDA} instance.
	 * @param blacklisted An array of {@link Long}s.
	 */
	public void blacklistGuilds(JDA jda, Long... blacklisted) {
		blacklistedGuilds = Arrays.stream(blacklisted).map(jda::getGuildById).collect(Collectors.toSet());
	}

	/**
	 * Allows a set of {@link Guild}s to update their Slash Commands.
	 *
	 * @param whitelisted An array of {@link Guild}s.
	 */
	public void whitelistGuilds(Guild... whitelisted) {
		whitelistedGuilds = Arrays.stream(whitelisted).collect(Collectors.toSet());
	}

	/**
	 * Prevents the given set of {@link Guild}s from updating their Slash Commands.
	 *
	 * @param blacklisted An array of {@link Guild}s.
	 */
	public void blacklistGuilds(Guild... blacklisted) {
		blacklistedGuilds = Arrays.stream(blacklisted).collect(Collectors.toSet());
	}

	/**
	 * Gets all Guilds whose Slash Commands should be updated.
	 *
	 * @param jda The {@link JDA} instance.
	 * @return A {@link List} with all Guilds.
	 */
	public Set<Guild> getGuilds(JDA jda) {
		Set<Guild> guilds = new HashSet<>(jda.getGuilds());
		guilds.removeIf(g -> blacklistedGuilds.contains(g));
		if (!whitelistedGuilds.isEmpty()) {
			guilds = whitelistedGuilds;
		}
		return guilds;
	}

	public boolean isGuildCommand() {
		return isGuildCommand;
	}

	public void setGuildCommand(boolean guildCommand) {
		isGuildCommand = guildCommand;
	}

	public Set<Permission> getRequiredPermissions() {
		return requiredPermissions;
	}

	public void requirePermissions(Permission... requiredPermissions) {
		this.requiredPermissions = Arrays.stream(requiredPermissions).collect(Collectors.toSet());
	}
}
