package com.dynxsty.dih4jda.interactions.context_command;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

public interface MessageContextCommand {
	void handleMessageContextCommand(MessageContextInteractionEvent event);
}
