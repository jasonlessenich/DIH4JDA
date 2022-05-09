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
public abstract class ExecutableCommand extends ComponentHandler {
	
	private final Set<Long> whitelistedGuilds = new HashSet<>();
	private final Set<Long> blacklistedGuilds = new HashSet<>();
	private final Set<Permission> requiredPermissions = new HashSet<>();
	private final Set<Long> requiredUsers = new HashSet<>();
	private boolean isGuildCommand = true;

	// TODO v1.5: Documentation
	public void requirePermissions(Permission... permissions) {
		if (!isGuildCommand) throw new UnsupportedOperationException("Cannot require User Permissions for Global Commands!");
		requiredPermissions.addAll(Arrays.asList(permissions));
	}

	// TODO v1.5: Documentation
	public void requireUsers(Long... users) {
		requiredUsers.addAll(Arrays.asList(users));
	}

	/**
	 * Allows a set of {@link Guild}s to update their Slash Commands.
	 *
	 * @param whitelisted An array of {@link Long}s.
	 */
	public void whitelistGuilds(Long... whitelisted) {
		if (!isGuildCommand) throw new UnsupportedOperationException("Cannot whitelist Guilds for Global Commands!");
		whitelistedGuilds.addAll(Arrays.asList(whitelisted));
	}

	/**
	 * Prevents the given set of {@link Guild}s from updating their Slash Commands.
	 *
	 * @param blacklisted An array of {@link Long}s.
	 */
	public void blacklistGuilds(Long... blacklisted) {
		if (!isGuildCommand) throw new UnsupportedOperationException("Cannot blacklist Guilds for Global Commands!");
		blacklistedGuilds.addAll(Arrays.asList(blacklisted));
	}

	/**
	 * Allows a set of {@link Guild}s to update their Slash Commands.
	 *
	 * @param whitelisted An array of {@link Guild}s.
	 */
	public void whitelistGuilds(Guild... whitelisted) {
		if (!isGuildCommand) throw new UnsupportedOperationException("Cannot whitelist Guilds for Global Commands!");
		whitelistedGuilds.addAll(Arrays.stream(whitelisted).map(Guild::getIdLong).collect(Collectors.toSet()));
	}

	/**
	 * Prevents the given set of {@link Guild}s from updating their Slash Commands.
	 *
	 * @param blacklisted An array of {@link Guild}s.
	 */
	public void blacklistGuilds(Guild... blacklisted) {
		if (!isGuildCommand) throw new UnsupportedOperationException("Cannot blacklist Guilds for Global Commands!");
		blacklistedGuilds.addAll(Arrays.stream(blacklisted).map(Guild::getIdLong).collect(Collectors.toSet()));
	}

	/**
	 * Gets all Guilds whose Slash Commands should be updated.
	 *
	 * @param jda The {@link JDA} instance.
	 * @return A {@link List} with all Guilds.
	 */
	public Set<Guild> getGuilds(JDA jda) {
		Set<Guild> guilds = new HashSet<>(jda.getGuilds());
		guilds.removeIf(g -> blacklistedGuilds.contains(g.getIdLong()));
		if (!whitelistedGuilds.isEmpty()) {
			guilds = whitelistedGuilds.stream().map(jda::getGuildById).collect(Collectors.toSet());
		}
		return guilds;
	}

	public boolean isGuildCommand() {
		return isGuildCommand;
	}

	// TODO v1.5: Documentation
	public void setGuildCommand(boolean guildCommand) {
		isGuildCommand = guildCommand;
	}

	public Set<Permission> getRequiredPermissions() {
		return requiredPermissions;
	}

	public Set<Long> getRequiredUsers() {
		return requiredUsers;
	}
}
