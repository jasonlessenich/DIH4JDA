package com.dynxsty.dih4jda.interactions.commands;

import net.dv8tion.jda.api.Permission;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class CommandRequirements extends ComponentHandler {
	private final Set<Permission> requiredPermissions = new HashSet<>();
	private final Set<Long> requiredUsers = new HashSet<>();

	// TODO v1.5: Documentation
	public void requirePermissions(Permission... permissions) {
		requiredPermissions.addAll(Arrays.asList(permissions));
	}

	// TODO v1.5: Documentation
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
