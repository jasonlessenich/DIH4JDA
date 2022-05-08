package com.dynxsty.dih4jda.interactions.commands;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

// TODO v1.5: Documentation
public abstract class SlashCommand extends ExecutableSlashCommand {

	private SlashCommandData data;
	private Class<? extends Subcommand>[] subcommands;
	private Class<? extends SubcommandGroup>[] subcommandGroups;

	protected SlashCommand() {}

	public SlashCommandData getCommandData() {
		return data;
	}

	// TODO v1.5: Documentation
	public void setCommandData(SlashCommandData commandData) {
		this.data = commandData;
	}

	public Class<? extends Subcommand>[] getSubcommands() {
		return subcommands;
	}

	// TODO v1.5: Documentation
	@SafeVarargs
	public final void setSubcommands(Class<? extends Subcommand>... classes) {
		this.subcommands = classes;
	}

	public Class<? extends SubcommandGroup>[] getSubcommandGroups() {
		return subcommandGroups;
	}

	// TODO v1.5: Documentation
	@SafeVarargs
	public final void setSubcommandGroups(Class<? extends SubcommandGroup>... classes) {
		this.subcommandGroups = classes;
	}

	// TODO v1.5: Documentation
	public abstract static class Subcommand extends ExecutableSlashCommand {
		private SubcommandData data;

		public SubcommandData getSubcommandData() {
			return data;
		}

		// TODO v1.5: Documentation
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

		// TODO v1.5: Documentation
		public void setSubcommandGroupData(SubcommandGroupData subcommandGroupData) {
			this.data = subcommandGroupData;
		}

		public Class<? extends Subcommand>[] getSubcommands() {
			return subcommands;
		}

		// TODO v1.5: Documentation
		@SafeVarargs
		public final void setSubcommands(Class<? extends Subcommand>... classes) {
			this.subcommands = classes;
		}
	}
}