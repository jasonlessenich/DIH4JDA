package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;

import java.time.Instant;

/**
 * An event that gets fired when the user, which invoked the command, is NOT allowed to use this command.
 *
 * @see xyz.dynxsty.dih4jda.interactions.commands.RestrictedCommand#setRequiredUsers(Long...)
 */
public class CommandCooldownEvent extends GenericDIH4JDAEvent<CommandInteraction> {

	private final Instant nextUse;

	public CommandCooldownEvent(DIH4JDA dih4jda, CommandInteraction interaction, Instant nextUse) {
		super("onCommandCooldown", dih4jda, interaction);
		this.nextUse = nextUse;
	}

	public Instant getNextUse() {
		return nextUse;
	}
}
