package com.dynxsty.dih4jda.commands.interactions.context_command;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

public interface IUserContextCommand {
	void handleUserContextInteraction(UserContextInteractionEvent event);
}
