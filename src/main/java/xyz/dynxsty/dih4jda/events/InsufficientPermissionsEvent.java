package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;

import java.util.Set;

public class InsufficientPermissionsEvent extends DIH4JDAEvent<CommandInteraction> {

	private final Set<Permission> permissions;

	public InsufficientPermissionsEvent(DIH4JDA dih4jda, CommandInteraction interaction, Set<Permission> permissions) {
		super("onInsufficientPermissions", dih4jda, interaction);
		this.permissions = permissions;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}
}
