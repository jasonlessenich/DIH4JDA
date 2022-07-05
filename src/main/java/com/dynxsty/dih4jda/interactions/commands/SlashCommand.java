package com.dynxsty.dih4jda.interactions.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a single Slash Command.
 *
 * @see SlashCommand#execute(SlashCommandInteractionEvent)
 * @since v1.5
 */
public abstract class SlashCommand extends BaseCommandRequirements {

	private SlashCommandData commandData = null;
	private Set<Subcommand> subcommands = Set.of();
	private Map<SubcommandGroupData, Set<Subcommand>> subcommandGroups = Map.of();

	private boolean handleAutoComplete = false;

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

	public final SlashCommandData getSlashCommandData() {
		return commandData;
	}

	/**
	 * Sets this commands' {@link SlashCommandData}.
	 *
	 * @param commandData The {@link SlashCommandData} which should be used for this command.
	 */
	public final void setSlashCommandData(SlashCommandData commandData) {
		this.commandData = commandData;
	}

	public final Set<Subcommand> getSubcommands() {
		return subcommands;
	}

	/**
	 * Sets all Subcommands that belong to this "base" command.
	 *
	 * @param classes The classes (must extend {@link Subcommand}) which should be registered as subcommands.
	 */
	public final void addSubcommands(Subcommand... classes) {
		this.subcommands = Arrays.stream(classes).collect(Collectors.toSet());
	}

	public final Map<SubcommandGroupData, Set<Subcommand>> getSubcommandGroups() {
		return subcommandGroups;
	}

	/**
	 * Sets all Subcommand Groups that belong to this "base" command.
	 *
	 * @param groups A map of the {@link SubcommandGroupData} and their corresponding {@link Subcommand}s.
	 */
	public final void addSubcommandGroups(Map<SubcommandGroupData, Set<Subcommand>> groups) {
		this.subcommandGroups = groups;
	}

	/**
	 * @return Whether the class handles all options that have the AutoComplete functionality activated.
	 * @since v1.4
	 */
	public final boolean isAutoCompleteHandling() {
		return handleAutoComplete;
	}

	/**
	 * Enables AutoComplete handling for all options of this Slash Command.
	 * If enabled, this class must implement {@link AutoCompletable} and
	 * override its method.
	 *
	 * <pre>{@code
	 * public class PingCommand extends SlashCommand implements AutoCompleteHandler {
	 *
	 *     public PingCommand() {
	 *         setCommandData(Commands.slash("ping", "Ping someone").addOption(OptionType.STRING, "user-id", "The user's id"));
	 *         enableAutoCompleteHandling();
	 *     }
	 *
	 *     @Override
	 *     public void execute(SlashCommandInteractionEvent event) {
	 *         OptionMapping mapping = event.getOption("user-id");
	 *         String userId = mapping.getAsString();
	 *         event.replyFormat("Ping! <@%s>", userId).queue();
	 *     }
	 *
	 *     @Override
	 *     public void handleAutoComplete(CommandAutoCompleteInteractionEvent event, AutoCompleteQuery target) {
	 *         if (target.getName().equals("user-id")) {
	 *             List<Member> members = event.getGuild().getMembers().stream().limit(25).collect(Collectors.toList());
	 *             List<Command.Choice> choices = new ArrayList<>(25);
	 *             for (Member member : members) {
	 *                 choices.add(new Command.Choice(member.getUser().getAsTag(), member.getId()));
	 *             }
	 *             event.replyChoices(AutoCompleteUtils.filterChoices(event, choices)).queue();
	 *         }
	 *     }
	 *
	 * }}</pre>
	 *
	 * @see AutoCompletable
	 * @see com.dynxsty.dih4jda.util.AutoCompleteUtils
	 * @since v1.4
	 */
	public final void setAutoCompleteHandling(boolean handleAutoComplete) {
		this.handleAutoComplete = handleAutoComplete;
	}

	/**
	 * Model class which represents a single Subcommand.
	 *
	 * @see SlashCommand.Subcommand#execute(SlashCommandInteractionEvent)
	 */
	public abstract static class Subcommand extends CommandRequirements {
		private SubcommandData data = null;
		private boolean handleAutoComplete = false;

		public final SubcommandData getSubcommandData() {
			return data;
		}

		/**
		 * Sets this subcommands' {@link SubcommandData}.
		 *
		 * @param subCommandData The {@link SubcommandData} which should be used for this subcommand.
		 * @see SubcommandData
		 */
		public final void setSubcommandData(SubcommandData subCommandData) {
			this.data = subCommandData;
		}

		/**
		 * @return Whether the class handles all options that have the AutoComplete functionality activated.
		 * @since v1.4
		 */
		public final boolean isAutoCompleteHandling() {
			return handleAutoComplete;
		}

		/**
		 * Enables AutoComplete handling for all options of this Slash Command.
		 * If enabled, this class must implement {@link AutoCompletable} and
		 * override its method.
		 *
		 * <pre>{@code
		 * public class PingCommand extends SlashCommand implements AutoCompleteHandler {
		 *
		 *     public PingCommand() {
		 *         setCommandData(Commands.slash("ping", "Ping someone").addOption(OptionType.STRING, "user-id", "The user's id"));
		 *         enableAutoCompleteHandling();
		 *     }
		 *
		 *     @Override
		 *     public void execute(SlashCommandInteractionEvent event) {
		 *         OptionMapping mapping = event.getOption("user-id");
		 *         String userId = mapping.getAsString();
		 *         event.replyFormat("Ping! <@%s>", userId).queue();
		 *     }
		 *
		 *     @Override
		 *     public void handleAutoComplete(CommandAutoCompleteInteractionEvent event, AutoCompleteQuery target) {
		 *         if (target.getName().equals("user-id")) {
		 *             List<Member> members = event.getGuild().getMembers().stream().limit(25).collect(Collectors.toList());
		 *             List<Command.Choice> choices = new ArrayList<>(25);
		 *             for (Member member : members) {
		 *                 choices.add(new Command.Choice(member.getUser().getAsTag(), member.getId()));
		 *             }
		 *             event.replyChoices(AutoCompleteUtils.filterChoices(event, choices)).queue();
		 *         }
		 *     }
		 *
		 * }}</pre>
		 *
		 * @see AutoCompletable
		 * @see com.dynxsty.dih4jda.util.AutoCompleteUtils
		 * @since v1.4
		 */
		public final void setAutoCompleteHandling(boolean handleAutoComplete) {
			this.handleAutoComplete = handleAutoComplete;
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
}