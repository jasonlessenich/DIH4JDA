package com.dynxsty.dih4jda.interactions.commands;

import net.dv8tion.jda.api.Permission;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Allows to set requirements that must be met in order to execute the command.
 * If a requirement isn't met, this will fire the corresponding event in {@link com.dynxsty.dih4jda.events.DIH4JDAListenerAdapter}.
 *
 * @since v1.5
 */
public abstract class CommandRequirements extends ComponentHandler {
	private final Set<Permission> requiredPermissions = new HashSet<>();
	private final Set<Long> requiredUsers = new HashSet<>();

	/**
	 * Allows to require a set of {@link Permission}s which are needed to execute the corresponding command.
	 *
	 * @param permissions The set of {@link Permission}s.
	 */
	public void requirePermissions(Permission... permissions) {
		requiredPermissions.addAll(Arrays.asList(permissions));
	}

	/**
	 * Allows to require a set of {@link Long}s (user Ids) which are able to execute the corresponding command.
	 *
	 * @param users The set of {@link Long}s (user Ids).
	 */
	public void requireUsers(Long... users) {
		requiredUsers.addAll(Arrays.asList(users));
	}

	public Set<Permission> getRequiredPermissions() {
		return requiredPermissions;
	}

	public Set<Long> getRequiredUsers() {
		return requiredUsers;
	}
}
