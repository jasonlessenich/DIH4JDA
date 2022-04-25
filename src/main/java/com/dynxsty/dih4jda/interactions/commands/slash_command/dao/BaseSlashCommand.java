package com.dynxsty.dih4jda.interactions.commands.slash_command.dao;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A SlashCommand object with getters, setters, a constructor and a toString method.
 */
public abstract class BaseSlashCommand {
	protected BaseSlashCommand() {
	}

	private SlashCommandData commandData;
	private Class<? extends Subcommand>[] subcommandClasses;
	private Class<? extends SubcommandGroup>[] subcommandGroupClasses;
	private CommandPrivilege[] commandPrivileges;

	private boolean handleAutoComplete = false;

	private List<String> handledButtonIds = new ArrayList<>();

	private List<String> handledSelectMenuIds = new ArrayList<>();;

	private List<String> handledModalIds = new ArrayList<>();;

	public SlashCommandData getCommandData() {
		return commandData;
	}

	public void setCommandData(SlashCommandData commandData) {
		this.commandData = commandData;
	}

	public Class<? extends Subcommand>[] getSubcommands() {
		return subcommandClasses;
	}

	@SafeVarargs
	public final void setSubcommands(Class<? extends Subcommand>... classes) {
		this.subcommandClasses = classes;
	}

	public Class<? extends SubcommandGroup>[] getSubcommandGroups() {
		return subcommandGroupClasses;
	}

	@SafeVarargs
	public final void setSubcommandGroups(Class<? extends SubcommandGroup>... classes) {
		this.subcommandGroupClasses = classes;
	}

	public CommandPrivilege[] getCommandPrivileges() {
		return commandPrivileges;
	}

	public void setCommandPrivileges(CommandPrivilege... commandPrivileges) {
		this.commandPrivileges = commandPrivileges;
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

	public void handleButtonIds(String... handledButtonIds) {
		this.handledButtonIds.addAll(Arrays.stream(handledButtonIds).collect(Collectors.toList()));
	}

	public List<String> getHandledSelectMenuIds() {
		return handledSelectMenuIds;
	}

	public void handleSelectMenuIds(String... handledSelectMenuIds) {
		this.handledSelectMenuIds.addAll(Arrays.stream(handledSelectMenuIds).collect(Collectors.toList()));
	}

	public List<String> getHandledModalIds() {
		return handledModalIds;
	}

	public void setHandledModalIds(String... handledModalIds) {
		this.handledModalIds.addAll(Arrays.stream(handledModalIds).collect(Collectors.toList()));
	}
}
