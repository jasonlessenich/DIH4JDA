package com.dynxsty.dih4jda.interactions.commands.slash_command;

import com.dynxsty.dih4jda.interactions.commands.slash_command.dao.ExecutableCommand;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

/**
 * Represents a single Slash Command Interaction.
 */
public class SlashCommandInteraction {

	private ExecutableCommand handler;
	private Class<?> baseClass;
	private CommandPrivilege[] privileges;

	public SlashCommandInteraction(ExecutableCommand handler, Class<?> baseClass, CommandPrivilege... privileges) {
		this.handler = handler;
		this.privileges = privileges;
		this.baseClass = baseClass;
	}

	public ExecutableCommand getHandler() {
		return handler;
	}

	public void setHandler(ExecutableCommand handler) {
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
