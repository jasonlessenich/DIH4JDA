package com.dynxsty.dih4jda.interactions.commands.slash_command.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract class that represents an executable Slash Command (excluding Subcommand Groups).
 */
public abstract class ExecutableCommand {

	protected ExecutableCommand() {
	}

	private boolean handleAutoComplete = false;

	private final List<String> handledButtonIds = new ArrayList<>();

	private final List<String> handledSelectMenuIds = new ArrayList<>();

	private final List<String> handledModalIds = new ArrayList<>();

	//TODO-v1.4: Documentation
	public boolean shouldHandleAutoComplete() {
		return handleAutoComplete;
	}

	//TODO-v1.4: Documentation
	public void enableAutoCompleteHandling() {
		handleAutoComplete = true;
	}

	//TODO-v1.4: Documentation
	public List<String> getHandledButtonIds() {
		return handledButtonIds;
	}

	//TODO-v1.4: Documentation
	public void handleButtonIds(String... handledButtonIds) {
		this.handledButtonIds.addAll(Arrays.stream(handledButtonIds).collect(Collectors.toList()));
	}

	//TODO-v1.4: Documentation
	public List<String> getHandledSelectMenuIds() {
		return handledSelectMenuIds;
	}

	//TODO-v1.4: Documentation
	public void handleSelectMenuIds(String... handledSelectMenuIds) {
		this.handledSelectMenuIds.addAll(Arrays.stream(handledSelectMenuIds).collect(Collectors.toList()));
	}

	//TODO-v1.4: Documentation
	public List<String> getHandledModalIds() {
		return handledModalIds;
	}

	//TODO-v1.4: Documentation
	public void handleModalIds(String... handledModalIds) {
		this.handledModalIds.addAll(Arrays.stream(handledModalIds).collect(Collectors.toList()));
	}
}
