package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.interactions.commands.AbstractCommand;

import java.util.Set;

/**
 * An event that gets fired when the user, which invoked the command, does NOT have one of the required permissions.
 *
 * @see AbstractCommand#setRequiredPermissions(Permission...)
 */
public class InsufficientPermissionsEvent extends GenericDIH4JDAEvent<CommandInteraction> {

	private final Set<Permission> permissions;

	public InsufficientPermissionsEvent(DIH4JDA dih4jda, CommandInteraction interaction, Set<Permission> permissions) {
		super("onInsufficientPermissions", dih4jda, interaction);
		this.permissions = permissions;
	}

	/**
	 * @return An immutable {@link Set} of all "required" permissions for the executed command.
	 * @see AbstractCommand#setRequiredPermissions(Permission...)
	 */
	public Set<Permission> getPermissions() {
		return permissions;
	}
}
