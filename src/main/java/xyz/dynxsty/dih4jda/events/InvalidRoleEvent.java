package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;

import java.util.Set;

public class InvalidRoleEvent extends GenericDIH4JDAEvent<CommandInteraction> {

	private final Set<Long> roleIds;

	public InvalidRoleEvent(DIH4JDA dih4jda, CommandInteraction interaction, Set<Long> roleIds) {
		super("onInvalidRole", dih4jda, interaction);
		this.roleIds = roleIds;
	}

	public Set<Long> getRoleIds() {
		return roleIds;
	}
}
