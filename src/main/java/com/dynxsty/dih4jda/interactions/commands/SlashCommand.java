package com.dynxsty.dih4jda.interactions.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.Map;

/**
 * Represents a single Slash Command.
 *
 * @since v1.5
 */
public abstract class SlashCommand extends Command implements ExecutableCommand<SlashCommandInteractionEvent> {

	private SlashCommandData data = null;
	private Subcommand[] subcommands = new Subcommand[]{};
	private Map<SubcommandGroupData, Subcommand[]> subcommandGroups = Map.of();

	protected SlashCommand() {
	}

	public final SlashCommandData getSlashCommandData() {
		return data;
	}

	/**
	 * Sets this commands' {@link SlashCommandData}.
	 *
	 * @param commandData The {@link SlashCommandData} which should be used for this command.
	 */
	public final void setSlashCommandData(SlashCommandData commandData) {
		this.data = commandData;
	}

	public final Subcommand[] getSubcommands() {
		return subcommands;
	}

	/**
	 * Sets all Subcommands that belong to this "base" command.
	 *
	 * @param classes The classes (must extend {@link Subcommand}) which should be registered as subcommands.
	 */
	public final void addSubcommands(Subcommand... classes) {
		this.subcommands = classes;
	}

	public final Map<SubcommandGroupData, Subcommand[]> getSubcommandGroups() {
		return subcommandGroups;
	}

	/**
	 * Sets all Subcommand Groups that belong to this "base" command.
	 *
	 * @param groups A map of the {@link SubcommandGroupData} and their corresponding {@link Subcommand}s.
	 */
	public final void addSubcommandGroups(Map<SubcommandGroupData, Subcommand[]> groups) {
		this.subcommandGroups = groups;
	}

	// TODO: Docs
	@Override
	public void execute(SlashCommandInteractionEvent event) {}

	/**
	 * Model class which represents a single Subcommand.
	 */
	public abstract static class Subcommand implements ExecutableCommand<SlashCommandInteractionEvent> {
		private SubcommandData data = null;

		public final SubcommandData getSubcommandData() {
			return data;
		}

		/**
		 * Sets this subcommands' {@link SubcommandData}.
		 *
		 * @param data The {@link SubcommandData} which should be used for this subcommand.
		 * @see SubcommandData
		 */
		public final void setSubcommandData(SubcommandData data) {
			this.data = data;
		}
	}
}