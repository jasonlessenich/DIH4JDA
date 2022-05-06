package com.dynxsty.dih4jda.interactions.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// TODO v1.5: Documentation
public class ExecutableCommand extends ComponentHandler implements GuildInteraction {


	private final Set<Long> whitelistedGuilds = new HashSet<>();
	private final Set<Long> blacklistedGuilds = new HashSet<>();
	protected Set<Permission> requiredPermissions = new HashSet<>();
	private boolean isGuildCommand = true;

	public Set<Permission> getRequiredPermissions() {
		return requiredPermissions;
	}

	// TODO v1.5: Documentation
	public void requirePermissions(Permission... requiredPermissions) {
		if (!isGuildCommand) throw new UnsupportedOperationException("Cannot require User Permissions for Global Commands!");
		this.requiredPermissions = Arrays.stream(requiredPermissions).collect(Collectors.toSet());
	}

	@Override
	public void whitelistGuilds(Long... whitelisted) {
		if (!isGuildCommand) throw new UnsupportedOperationException("Cannot whitelist Guilds for Global Commands!");
		whitelistedGuilds.addAll(Arrays.asList(whitelisted));
	}

	@Override
	public void blacklistGuilds(Long... blacklisted) {
		if (!isGuildCommand) throw new UnsupportedOperationException("Cannot blacklist Guilds for Global Commands!");
		blacklistedGuilds.addAll(Arrays.asList(blacklisted));
	}

	@Override
	public void whitelistGuilds(Guild... whitelisted) {
		if (!isGuildCommand) throw new UnsupportedOperationException("Cannot whitelist Guilds for Global Commands!");
		whitelistedGuilds.addAll(Arrays.stream(whitelisted).map(Guild::getIdLong).collect(Collectors.toSet()));
	}

	@Override
	public void blacklistGuilds(Guild... blacklisted) {
		if (!isGuildCommand) throw new UnsupportedOperationException("Cannot blacklist Guilds for Global Commands!");
		blacklistedGuilds.addAll(Arrays.stream(blacklisted).map(Guild::getIdLong).collect(Collectors.toSet()));
	}

	@Override
	public Set<Guild> getGuilds(JDA jda) {
		Set<Guild> guilds = new HashSet<>(jda.getGuilds());
		guilds.removeIf(g -> blacklistedGuilds.contains(g.getIdLong()));
		if (!whitelistedGuilds.isEmpty()) {
			guilds = whitelistedGuilds.stream().map(jda::getGuildById).collect(Collectors.toSet());
		}
		return guilds;
	}

	@Override
	public boolean isGuildCommand() {
		return isGuildCommand;
	}

	// TODO v1.5: Documentation
	@Override
	public void setGuildCommand(boolean guildCommand) {
		isGuildCommand = guildCommand;
	}
}
