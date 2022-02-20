package com.dynxsty.dih4jda.commands.interactions.context;

/**
 * Represents a single Slash Command Interaction.
 */
public class MessageContextInteraction {

	private IMessageContextCommand handler;

	public MessageContextInteraction(IMessageContextCommand handler) {
		this.setHandler(handler);
	}

	public IMessageContextCommand getHandler() {
		return handler;
	}

	public void setHandler(IMessageContextCommand handler) {
		this.handler = handler;
	}
}
