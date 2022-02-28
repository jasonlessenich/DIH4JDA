package com.dynxsty.dih4jda.commands.interactions.slash_command;

import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

/**
 * Represents a single Slash Command Interaction.
 */
public class SlashCommandInteraction {

	private ISlashCommand handler;
	private Class<?> baseClass;
	private CommandPrivilege[] privileges;

	public SlashCommandInteraction(ISlashCommand handler, Class<?> baseClass, CommandPrivilege... privileges) {
		this.handler = handler;
		this.privileges = privileges;
		this.baseClass = baseClass;
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

	public Class<?> getBaseClass() {
		return baseClass;
	}

	public void setBaseClass(Class<?> baseClass) {
		this.baseClass = baseClass;
	}
}
