package xyz.dynxsty.dih4jda.events;

import lombok.Getter;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.interactions.commands.RestrictedCommand;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * An event that gets fired when the user, which invoked the command, is NOT allowed to use this command.
 *
 * @see RestrictedCommand#setRequiredUsers(Long...)
 */
public class InvalidUserEvent extends DIH4JDAEvent<CommandInteraction> {

	@Getter
	private final Set<Long> userIds;

	/**
	 * Creates a new instance of the {@link InvalidUserEvent}.
	 *
	 * @param dih4jda the {@link DIH4JDA} instance that fired the event.
	 * @param interaction the {@link CommandInteraction}.
	 * @param userIds the user ids that was allowed to execute {@link RestrictedCommand}.
	 */
	public InvalidUserEvent(@Nonnull DIH4JDA dih4jda, @Nonnull CommandInteraction interaction,
							@Nonnull Set<Long> userIds) {
		super("onInvalidUser", dih4jda, interaction);
		this.userIds = userIds;
	}
}
