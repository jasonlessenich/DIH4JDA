package com.dynxsty.dih4jda.interactions.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

/**
 * Represents a single Slash Command.
 *
 * @see SlashCommand#execute(SlashCommandInteractionEvent)
 * @since v1.5
 */
public abstract class SlashCommand extends ExecutableCommand {

	private SlashCommandData data;
	private Class<? extends Subcommand>[] subcommands;
	private Class<? extends SubcommandGroup>[] subcommandGroups;

	protected SlashCommand() {
	}

	/**
	 * Method that should be overridden for all Slash Commands that should be executed.
	 *
	 * <pre>{@code
	 * public class PingCommand extends SlashCommand {
	 *
	 *     public PingCommand() {
	 *         setCommandData(Commands.slash("ping", "Ping!"));
	 *     }
	 *
	 *    @Override
	 *    public void execute(SlashCommandInteractionEvent event) {
	 * 		event.reply("Pong!").queue();
	 *    }
	 *
	 * }
	 * }</pre>
	 *
	 * @since v1.5
	 */
	public void execute(SlashCommandInteractionEvent event) {
	}

	public SlashCommandData getCommandData() {
		return data;
	}

	/**
	 * Sets this commands' {@link SlashCommandData}.
	 *
	 * @param commandData The {@link SlashCommandData} which should be used for this command.
	 * @see {@link net.dv8tion.jda.api.interactions.commands.build.Commands#slash(String, String)}
	 */
	public void setCommandData(SlashCommandData commandData) {
		this.data = commandData;
	}

	public Class<? extends Subcommand>[] getSubcommands() {
		return subcommands;
	}

	/**
	 * Sets all Subcommands that belong to this "base" command.
	 *
	 * @param classes The classes (must extend {@link Subcommand}) which should be registered as subcommands.
	 */
	@SafeVarargs
	public final void setSubcommands(Class<? extends Subcommand>... classes) {
		this.subcommands = classes;
	}

	public Class<? extends SubcommandGroup>[] getSubcommandGroups() {
		return subcommandGroups;
	}

	/**
	 * Sets all Subcommand Groups that belong to this "base" command.
	 *
	 * @param classes The classes (must extend {@link SubcommandGroup}) which should be registered as subcommand groups.
	 */
	@SafeVarargs
	public final void setSubcommandGroups(Class<? extends SubcommandGroup>... classes) {
		this.subcommandGroups = classes;
	}

	/**
	 * Model class which represents a single Subcommand.
	 *
	 * @see SlashCommand.Subcommand#execute(SlashCommandInteractionEvent)
	 */
	public abstract static class Subcommand extends CommandRequirements {
		private SubcommandData data;

		public SubcommandData getSubcommandData() {
			return data;
		}

		/**
		 * Sets this subcommands' {@link SubcommandData}.
		 *
		 * @param subCommandData The {@link SubcommandData} which should be used for this subcommand.
		 * @see SubcommandData
		 */
		public void setSubcommandData(SubcommandData subCommandData) {
			this.data = subCommandData;
		}

		/**
		 * Method that should be overridden for all Slash Commands that should be executed.
		 *
		 * <pre>{@code
		 * public class PingCommand extends SlashCommand.Subcommand {
		 *
		 *     public PingCommand() {
		 *         setSubcommandData(Commands.slash("ping", "Ping!"));
		 *     }
		 *
		 *    @Override
		 *    public void execute(SlashCommandInteractionEvent event) {
		 * 		event.reply("Pong!").queue();
		 *    }
		 *
		 * }
		 * }</pre>
		 *
		 * @since v1.5
		 */
		public abstract void execute(SlashCommandInteractionEvent event);
	}

	/**
	 * Model class which represents a single Subcommand Group
	 */
	public abstract static class SubcommandGroup {
		private SubcommandGroupData data;
		private Class<? extends Subcommand>[] subcommands;

		public SubcommandGroupData getSubcommandGroupData() {
			return data;
		}

		/**
		 * Sets this group' {@link SubcommandGroupData}.
		 *
		 * @param subcommandGroupData The {@link SubcommandGroupData} which should be used for this subcommand group.
		 * @see SubcommandGroupData
		 */
		public void setSubcommandGroupData(SubcommandGroupData subcommandGroupData) {
			this.data = subcommandGroupData;
		}

		public Class<? extends Subcommand>[] getSubcommands() {
			return subcommands;
		}

		/**
		 * Sets all Subcommands that belong to this subcommand group.
		 *
		 * @param classes The classes (must extend {@link Subcommand}) which should be registered as subcommands
		 *                of this subcommand group.
		 */
		@SafeVarargs
		public final void setSubcommands(Class<? extends Subcommand>... classes) {
			this.subcommands = classes;
		}
	}
}