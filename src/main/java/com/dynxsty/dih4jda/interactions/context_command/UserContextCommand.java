package com.dynxsty.dih4jda.interactions.context_command;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

public interface UserContextCommand {
	void handleUserContextCommand(UserContextInteractionEvent event);
}
