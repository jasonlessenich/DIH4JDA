package com.dynxsty.dih4jda.interactions.commands.context_command.dao;

import com.dynxsty.dih4jda.DIH4JDALogger;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;

public abstract class BaseContextCommand {
	protected BaseContextCommand() {}

	private CommandData commandData;

	private boolean handleAutoComplete;

	private List<String> handledButtonIds;

	private List<String> handledSelectMenuIds;

	private List<String> handledModalIds;

	public void setCommandData(CommandData commandData) {
		if (commandData.getType() == Command.Type.MESSAGE || commandData.getType() == Command.Type.USER) {
			this.commandData = commandData;
		} else {
			DIH4JDALogger.error(String.format("Invalid Command Type \"%s\" for Context Command! This command will be ignored.", commandData.getType()));
		}
	}

	public CommandData getCommandData() {
		return commandData;
	}

	public boolean shouldHandleAutoComplete() {
		return handleAutoComplete;
	}

	public void setAutoCompleteHandling(boolean handleAutoComplete) {
		this.handleAutoComplete = handleAutoComplete;
	}

	public List<String> getHandledButtonIds() {
		return handledButtonIds;
	}

	public void setHandledButtonIds(List<String> handledButtonIds) {
		this.handledButtonIds = handledButtonIds;
	}

	public List<String> getHandledSelectMenuIds() {
		return handledSelectMenuIds;
	}

	public void setHandledSelectMenuIds(List<String> handledSelectMenuIds) {
		this.handledSelectMenuIds = handledSelectMenuIds;
	}

	public List<String> getHandledModalIds() {
		return handledModalIds;
	}

	public void setHandledModalIds(List<String> handledModalIds) {
		this.handledModalIds = handledModalIds;
	}
}
