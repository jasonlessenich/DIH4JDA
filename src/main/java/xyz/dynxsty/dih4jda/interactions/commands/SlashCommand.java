package xyz.dynxsty.dih4jda.interactions.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import xyz.dynxsty.dih4jda.InteractionHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single Slash Command.
 *
 * @since v1.5
 */
public abstract class SlashCommand extends AbstractCommand implements ExecutableCommand<SlashCommandInteractionEvent> {

	private SlashCommandData data = null;
	private Subcommand[] subcommands = new Subcommand[]{};
	private SubcommandGroup[] subcommandGroups = new SubcommandGroup[]{};

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
		for (Subcommand subcommand : classes) {
			subcommand.parent = this;
		}
		this.subcommands = classes;
	}

	public final SubcommandGroup[] getSubcommandGroups() {
		return subcommandGroups;
	}

	/**
	 * Sets all Subcommand Groups that belong to this "base" command.
	 *
	 * @param groups A map of the {@link SubcommandGroupData} and their corresponding {@link Subcommand}s.
	 */
	public final void addSubcommandGroups(SubcommandGroup... groups) {
		for (SubcommandGroup group : groups) {
			for (Subcommand subcommand : group.getSubcommands()) {
				subcommand.parent = this;
			}
		}
		this.subcommandGroups = groups;
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {}

	public @Nullable Command asCommand() {
		if (data == null) return null;
		return InteractionHandler.getRetrievedCommands().get(data.getName());
	}

	/**
	 * Model class which represents a single Subcommand.
	 */
	public abstract static class Subcommand implements ExecutableCommand<SlashCommandInteractionEvent> {
		private SubcommandData data = null;
		private SlashCommand parent = null;

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

		public SlashCommand getParent() {
			return parent;
		}

		public @Nullable Command.Subcommand asSubcommand() {
			if (data == null || parent == null) return null;
			Command cmd = parent.asCommand();
			if (cmd == null) return null;
			List<Command.Subcommand> subcommands = new ArrayList<>(cmd.getSubcommands());
			cmd.getSubcommandGroups().forEach(g -> subcommands.addAll(g.getSubcommands()));
			return subcommands.stream()
					.filter(c -> c.getName().equals(data.getName()))
					.findFirst()
					.orElse(null);
		}
	}

	/**
	 * Model class which represents a single subcommand group.
	 * This simply holds the {@link SubcommandGroupData} and an array of all {@link Subcommand}s.
	 */
	public static class SubcommandGroup {
		private final SubcommandGroupData data;
		private final Subcommand[] subcommands;

		private SubcommandGroup(SubcommandGroupData data, Subcommand... subcommands) {
			this.data = data;
			this.subcommands = subcommands;
		}

		/**
		 * Creates a new instance of the {@link SubcommandGroup} class.
		 *
		 * @param data The {@link SubcommandGroupData} to use.
		 * @param subcommands An array of {@link Subcommand}s. This should NOT be empty!
		 * @return The {@link SubcommandGroup}.
		 */
		@Nonnull
		public static SubcommandGroup of(SubcommandGroupData data, Subcommand... subcommands) {
			return new SubcommandGroup(data, subcommands);
		}

		/**
		 * @return The corresponding {@link SubcommandGroupData}.
		 */
		public SubcommandGroupData getData() {
			return data;
		}

		/**
		 * @return An array of {@link Subcommand}s.
		 */
		public Subcommand[] getSubcommands() {
			return subcommands;
		}
	}
}