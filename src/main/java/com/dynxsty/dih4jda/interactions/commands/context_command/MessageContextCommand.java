package com.dynxsty.dih4jda.interactions.commands.context_command;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

//TODO-v1.4: Documentation
public interface MessageContextCommand {
	void handleMessageContextCommand(MessageContextInteractionEvent event);
}
