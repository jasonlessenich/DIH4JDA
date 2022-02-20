package com.dynxsty.dih4jda.commands.interactions.context;

/**
 * Represents a single Slash Command Interaction.
 */
public class UserContextInteraction {

	private IUserContextCommand handler;

	public UserContextInteraction(IUserContextCommand handler) {
		this.setHandler(handler);
	}

	public IUserContextCommand getHandler() {
		return handler;
	}

	public void setHandler(IUserContextCommand handler) {
		this.handler = handler;
	}
}
