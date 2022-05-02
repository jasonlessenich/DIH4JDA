package com.dynxsty.dih4jda.interactions.commands.slash_command.dao;

import com.dynxsty.dih4jda.interactions.commands.ExecutableCommand;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

/**
 * A SlashCommand object with getters, setters, a constructor and a toString method.
 */
public abstract class BaseSlashCommand extends ExecutableCommand {
	private SlashCommandData commandData;
	private Class<? extends Subcommand>[] subcommandClasses;
	private Class<? extends SubcommandGroup>[] subcommandGroupClasses;
	private CommandPrivilege[] commandPrivileges;

	protected BaseSlashCommand() {
	}

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
}
