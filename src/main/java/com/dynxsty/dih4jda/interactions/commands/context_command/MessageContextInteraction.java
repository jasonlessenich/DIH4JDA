package com.dynxsty.dih4jda.interactions.commands.context_command;

/**
 * Represents a single Slash Command Interaction.
 */
public class MessageContextInteraction {

	private MessageContextCommand handler;

	public MessageContextInteraction(MessageContextCommand handler) {
		this.setHandler(handler);
	}

	public MessageContextCommand getHandler() {
		return handler;
	}

	public void setHandler(MessageContextCommand handler) {
		this.handler = handler;
	}
}
