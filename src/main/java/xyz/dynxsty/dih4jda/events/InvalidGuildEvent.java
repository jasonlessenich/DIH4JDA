package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.interactions.commands.RestrictedCommand;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * An event that gets fired when the command is NOT executed in one of the required guild.
 *
 * @see RestrictedCommand#setRequiredGuilds(Long...)
 */
public class InvalidGuildEvent extends DIH4JDAInteractionEvent<CommandInteraction> {

	private final Set<Long> guildIds;

	/**
	 * Creates a new instance {@link InvalidGuildEvent}.
	 *
	 * @param dih4jda the {@link DIH4JDA} instance that fired this event.
	 * @param interaction the {@link CommandInteraction}.
	 * @param guildIds the guild ids that the {@link RestrictedCommand} could be executed on.
	 */
	public InvalidGuildEvent(@Nonnull DIH4JDA dih4jda, @Nonnull CommandInteraction interaction,
							 @Nonnull Set<Long> guildIds) {
		super("onInvalidGuild", dih4jda, interaction);
		this.guildIds = guildIds;
	}

	/**
	 * The guild ids that the commands could only be executed on.
	 *
	 * @return An immutable {@link Set} of all "required" guilds for the executed command.
	 * @see RestrictedCommand#setRequiredGuilds(Long...)
	 */
	@Nonnull
	public Set<Long> getGuildIds() {
		return guildIds;
	}
}
