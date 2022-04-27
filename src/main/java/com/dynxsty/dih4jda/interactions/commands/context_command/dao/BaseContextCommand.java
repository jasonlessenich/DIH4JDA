package com.dynxsty.dih4jda.interactions.commands.context_command.dao;

import com.dynxsty.dih4jda.DIH4JDALogger;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public abstract class BaseContextCommand {
	private CommandData commandData;

	protected BaseContextCommand() {
	}

	public CommandData getCommandData() {
		return commandData;
	}

	public void setCommandData(CommandData commandData) {
		if (commandData.getType() == Command.Type.MESSAGE || commandData.getType() == Command.Type.USER) {
			this.commandData = commandData;
		} else {
			DIH4JDALogger.error(String.format("Invalid Command Type \"%s\" for Context Command! This command will be ignored.", commandData.getType()));
		}
	}
}
