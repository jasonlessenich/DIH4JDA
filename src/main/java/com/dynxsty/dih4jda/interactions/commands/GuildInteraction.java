package com.dynxsty.dih4jda.interactions.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;
import java.util.Set;

// TODO v1.5: Documentation
public interface GuildInteraction {

	/**
	 * Allows a set of {@link Guild}s to update their Slash Commands.
	 *
	 * @param whitelisted An array of {@link Long}s.
	 */
	void whitelistGuilds(Long... whitelisted);

	/**
	 * Prevents the given set of {@link Guild}s from updating their Slash Commands.
	 *
	 * @param blacklisted An array of {@link Long}s.
	 */
	void blacklistGuilds(Long... blacklisted);

	/**
	 * Allows a set of {@link Guild}s to update their Slash Commands.
	 *
	 * @param whitelisted An array of {@link Guild}s.
	 */
	void whitelistGuilds(Guild... whitelisted);

	/**
	 * Prevents the given set of {@link Guild}s from updating their Slash Commands.
	 *
	 * @param blacklisted An array of {@link Guild}s.
	 */
	void blacklistGuilds(Guild... blacklisted);

	/**
	 * Gets all Guilds whose Slash Commands should be updated.
	 *
	 * @param jda The {@link JDA} instance.
	 * @return A {@link List} with all Guilds.
	 */
	Set<Guild> getGuilds(JDA jda);

	boolean isGuildCommand();

	void setGuildCommand(boolean guildCommand);
}
