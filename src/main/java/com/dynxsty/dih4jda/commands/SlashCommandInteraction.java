package com.dynxsty.dih4jda.commands;

import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

/**
 * Represents a single Slash Command Interaction.
 */
public class SlashCommandInteraction {

	private ISlashCommand handler;
	private CommandPrivilege[] privileges;

	public SlashCommandInteraction(ISlashCommand handler, CommandPrivilege... privileges) {
		this.setHandler(handler);
		this.setPrivileges(privileges);
	}

	public ISlashCommand getHandler() {
		return handler;
	}

	public void setHandler(ISlashCommand handler) {
		this.handler = handler;
	}

	public CommandPrivilege[] getPrivileges() {
		return privileges;
	}

	public void setPrivileges(CommandPrivilege[] privileges) {
		this.privileges = privileges;
	}
}
