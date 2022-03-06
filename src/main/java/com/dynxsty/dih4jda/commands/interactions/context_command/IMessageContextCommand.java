package com.dynxsty.dih4jda.commands.interactions.context_command;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

public interface IMessageContextCommand {
	void handleMessageContextInteraction(MessageContextInteractionEvent event);
}
