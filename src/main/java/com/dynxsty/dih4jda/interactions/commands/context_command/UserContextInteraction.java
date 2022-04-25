package com.dynxsty.dih4jda.interactions.commands.context_command;

/**
 * Represents a single Slash Command Interaction.
 */
public class UserContextInteraction {

	private UserContextCommand handler;

	public UserContextInteraction(UserContextCommand handler) {
		this.setHandler(handler);
	}

	public UserContextCommand getHandler() {
		return handler;
	}

	public void setHandler(UserContextCommand handler) {
		this.handler = handler;
	}
}
