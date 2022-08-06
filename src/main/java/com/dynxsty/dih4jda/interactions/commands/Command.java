package com.dynxsty.dih4jda.interactions.commands;

import com.dynxsty.dih4jda.DIH4JDA;
import com.dynxsty.dih4jda.util.Pair;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: Docs
public abstract class Command {
	private RegistrationType type = DIH4JDA.defaultCommandType;

	private Pair<Boolean, Long[]> requiredGuilds = new Pair<>(null, null);
	private Permission[] requiredPermissions = new Permission[]{};
	private Long[] requiredUsers = new Long[]{};
	private Long[] requiredRoles = new Long[]{};

	public final RegistrationType getRegistrationType() {
		return type;
	}

	/**
	 * @param type How the command should be queued.
	 */
	public final void setRegistrationType(RegistrationType type) {
		this.type = type;
	}

	// TODO: Better Docs
	// TODO: Check Subcommand
	/**
	 * Allows a set of {@link Guild}s to update their Slash Commands.
	 *
	 * @param restrictQueue If enabled, this will only queue this command in the specified guilds. For that
	 *                      to work, the command MUST be of {@link RegistrationType#GUILD}. If
	 *                      {@link com.dynxsty.dih4jda.DIH4JDABuilder#setGuildSmartQueue(boolean)} is enabled, this
	 *                      will also delete the command in all the other guilds. This DOES NOT work with {@link SlashCommand.Subcommand}!
	 * @param guilds An array of {@link Long}s.
	 */
	public final void setRequiredGuilds(boolean restrictQueue, Long... guilds) {
		if (restrictQueue && type != RegistrationType.GUILD) {
			throw new UnsupportedOperationException("Cannot restrict queue for Global Commands!");
		}
		requiredGuilds = new Pair<>(restrictQueue, guilds);
	}

	// TODO: Better Docs
	/**
	 * Allows a set of {@link Guild}s to update their Slash Commands.
	 *
	 * @param guilds An array of {@link Long}s.
	 */
	public final void setRequiredGuilds(Long... guilds) {
		setRequiredGuilds(false, guilds);
	}

	/**
	 * Allows to require a set of {@link Permission}s which are needed to execute the corresponding command.
	 *
	 * @param permissions The set of {@link Permission}s.
	 */
	public final void setRequiredPermissions(Permission... permissions) {
		requiredPermissions = permissions;
	}

	/**
	 * Allows to require a set of {@link Long}s (user Ids) which are able to execute the corresponding command.
	 *
	 * @param users The set of {@link Long}s (user Ids).
	 */
	public final void setRequiredUsers(Long... users) {
		requiredUsers = users;
	}

	/**
	 * Allows to require a set of {@link Long}s (role Ids) which are able to execute the corresponding command.
	 *
	 * @param roles The set of {@link Long}s (role Ids).
	 */
	public final void setRequiredRoles(Long... roles) {
		requiredRoles = roles;
	}

	public Pair<Boolean, Long[]> getRequiredGuilds() {
		return requiredGuilds;
	}

	public final Permission[] getRequiredPermissions() {
		return requiredPermissions;
	}

	public final Long[] getRequiredUsers() {
		return requiredUsers;
	}

	public final Long[] getRequiredRoles() {
		return requiredRoles;
	}
}
