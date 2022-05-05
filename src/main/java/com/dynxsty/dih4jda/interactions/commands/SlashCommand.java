package com.dynxsty.dih4jda.interactions.commands;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

// TODO v1.5: Documentation
public abstract class SlashCommand extends GuildInteraction implements ExecutableSlashCommand {

	private SlashCommandData data;
	private Class<? extends Subcommand>[] subcommands;
	private Class<? extends SubcommandGroup>[] subcommandGroups;
	private CommandPrivilege[] privileges;

	protected SlashCommand() {
	}

	public SlashCommandData getCommandData() {
		return data;
	}

	public void setCommandData(SlashCommandData commandData) {
		this.data = commandData;
	}

	public Class<? extends Subcommand>[] getSubcommands() {
		return subcommands;
	}

	@SafeVarargs
	public final void setSubcommands(Class<? extends Subcommand>... classes) {
		this.subcommands = classes;
	}

	public Class<? extends SubcommandGroup>[] getSubcommandGroups() {
		return subcommandGroups;
	}

	@SafeVarargs
	public final void setSubcommandGroups(Class<? extends SubcommandGroup>... classes) {
		this.subcommandGroups = classes;
	}

	public CommandPrivilege[] getCommandPrivileges() {
		return privileges;
	}

	public void setCommandPrivileges(CommandPrivilege... commandPrivileges) {
		this.privileges = commandPrivileges;
	}

	// TODO v1.5: Documentation
	public abstract static class Subcommand extends ComponentHandler implements ExecutableSlashCommand {
		private SubcommandData data;

		public SubcommandData getSubcommandData() {
			return data;
		}

		public void setSubcommandData(SubcommandData subCommandData) {
			this.data = subCommandData;
		}
	}

	// TODO v1.5: Documentation
	public abstract static class SubcommandGroup {
		private SubcommandGroupData data;
		private Class<? extends Subcommand>[] subcommands;

		public SubcommandGroupData getSubcommandGroupData() {
			return data;
		}

		public void setSubcommandGroupData(SubcommandGroupData subcommandGroupData) {
			this.data = subcommandGroupData;
		}

		public Class<? extends Subcommand>[] getSubcommands() {
			return subcommands;
		}

		@SafeVarargs
		public final void setSubcommands(Class<? extends Subcommand>... classes) {
			this.subcommands = classes;
		}
	}
}
