package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.interactions.commands.RestrictedCommand;

import java.util.Set;

/**
 * An event that gets fired when the command is NOT executed in one of the required guild.
 *
 * @see RestrictedCommand#setRequiredGuilds(Long...)
 */
public class InvalidGuildEvent extends DIH4JDAEvent<CommandInteraction> {

	private final Set<Long> guildIds;

	public InvalidGuildEvent(DIH4JDA dih4jda, CommandInteraction interaction, Set<Long> guildIds) {
		super("onInvalidGuild", dih4jda, interaction);
		this.guildIds = guildIds;
	}

	/**
	 * @return An immutable {@link Set} of all "required" guilds for the executed command.
	 * @see RestrictedCommand#setRequiredGuilds(Long...)
	 */
	public Set<Long> getGuildIds() {
		return guildIds;
	}
}
