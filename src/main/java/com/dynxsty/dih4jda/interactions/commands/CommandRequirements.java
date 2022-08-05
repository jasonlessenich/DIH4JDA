package com.dynxsty.dih4jda.interactions.commands;

import com.dynxsty.dih4jda.events.DIH4JDAEventListener;
import net.dv8tion.jda.api.Permission;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Allows to set requirements that must be met in order to execute the command.
 * If a requirement isn't met, this will fire the corresponding event in {@link DIH4JDAEventListener}.
 *
 * @since v1.5
 */
public abstract class CommandRequirements {
	private Set<Permission> requiredPermissions = Set.of();
	private Set<Long> requiredUsers = Set.of();
	private Set<Long> requiredRoles = Set.of();

	/**
	 * Allows to require a set of {@link Permission}s which are needed to execute the corresponding command.
	 *
	 * @param permissions The set of {@link Permission}s.
	 */
	public final void requirePermissions(Permission... permissions) {
		if (permissions == null) {
			throw new IllegalArgumentException("permisisons cannot be null");
		}
		requiredPermissions = Arrays.stream(permissions).collect(Collectors.toSet());
	}

	/**
	 * Allows to require a set of {@link Long}s (user Ids) which are able to execute the corresponding command.
	 *
	 * @param users The set of {@link Long}s (user Ids).
	 */
	public final void requireUsers(Long... users) {
		if (users == null) {
			throw new IllegalArgumentException("users cannot be null");
		}
		requiredUsers = Arrays.stream(users).collect(Collectors.toSet());
	}

	/**
	 * Allows to require a set of {@link Long}s (role Ids) which are able to execute the corresponding command.
	 *
	 * @param roles The set of {@link Long}s (role Ids).
	 */
	public final void requireRoles(Long... roles) {
		if (roles == null) {
			throw new IllegalArgumentException("permissions cannot be null");
		}
		requiredRoles = Arrays.stream(roles).collect(Collectors.toSet());
	}

	public final Set<Permission> getRequiredPermissions() {
		return requiredPermissions;
	}

	public final Set<Long> getRequiredUsers() {
		return requiredUsers;
	}

	public final Set<Long> getRequiredRoles() {
		return requiredRoles;
	}
}
