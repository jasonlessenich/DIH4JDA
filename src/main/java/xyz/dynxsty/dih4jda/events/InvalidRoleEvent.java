package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.interactions.commands.AbstractCommand;

import java.util.Set;

/**
 * An event that gets fired when the user, which invoked the command, does NOT have the required roles to use this command.
 *
 * @see AbstractCommand#setRequiredRoles(Long...)
 */
public class InvalidRoleEvent extends GenericDIH4JDAEvent<CommandInteraction> {

	private final Set<Long> roleIds;

	public InvalidRoleEvent(DIH4JDA dih4jda, CommandInteraction interaction, Set<Long> roleIds) {
		super("onInvalidRole", dih4jda, interaction);
		this.roleIds = roleIds;
	}

	/**
	 * @return An immutable {@link Set} of all "required" roles for the executed command.
	 * @see AbstractCommand#setRequiredRoles(Long...)
	 */
	public Set<Long> getRoleIds() {
		return roleIds;
	}
}
