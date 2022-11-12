package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.interactions.commands.RestrictedCommand;

import java.util.Set;

/**
 * An event that gets fired when the user, which invoked the command, is NOT allowed to use this command.
 *
 * @see RestrictedCommand#setRequiredUsers(Long...)
 */
public class InvalidUserEvent extends DIH4JDAEvent<CommandInteraction> {

	private final Set<Long> userIds;

	public InvalidUserEvent(DIH4JDA dih4jda, CommandInteraction interaction, Set<Long> userIds) {
		super("onInvalidUser", dih4jda, interaction);
		this.userIds = userIds;
	}

	/**
	 * @return An immutable {@link Set} of all "required" users for the executed command.
	 * @see RestrictedCommand#setRequiredUsers(Long...)
	 */
	public Set<Long> getUserIds() {
		return userIds;
	}
}
