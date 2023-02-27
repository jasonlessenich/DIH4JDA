package xyz.dynxsty.dih4jda.events;

import lombok.Getter;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.interactions.commands.RestrictedCommand;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * An event that gets fired when the user, which invoked the command, does NOT have the required roles to use this command.
 *
 * @see RestrictedCommand#setRequiredRoles(Long...)
 */
public class InvalidRoleEvent extends DIH4JDAEvent<CommandInteraction> {

	@Getter
	private final Set<Long> roleIds;

	/**
	 * Create a new instance of the {@link InvalidRoleEvent}.
	 *
	 * @param dih4jda the {@link DIH4JDA} that fired the event.
	 * @param interaction the {@link CommandInteraction}.
	 * @param roleIds the role ids that was allowed to execute the {@link RestrictedCommand}.
	 */
	public InvalidRoleEvent(@Nonnull DIH4JDA dih4jda, @Nonnull CommandInteraction interaction, @Nonnull Set<Long> roleIds) {
		super("onInvalidRole", dih4jda, interaction);
		this.roleIds = roleIds;
	}
}
