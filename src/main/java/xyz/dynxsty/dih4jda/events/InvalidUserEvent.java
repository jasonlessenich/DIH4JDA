package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;

import java.util.Set;

public class InvalidUserEvent extends GenericDIH4JDAEvent<CommandInteraction> {

	private final Set<Long> userIds;

	public InvalidUserEvent(DIH4JDA dih4jda, CommandInteraction interaction, Set<Long> userIds) {
		super("onInvalidUser", dih4jda, interaction);
		this.userIds = userIds;
	}

	public Set<Long> getUserIds() {
		return userIds;
	}
}
