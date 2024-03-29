package xyz.dynxsty.dih4jda.interactions.commands.application;

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
public abstract class SlashCommand extends BaseApplicationCommand<SlashCommandInteractionEvent, SlashCommandData> {
	private Subcommand[] subcommands = new Subcommand[]{};
	private SubcommandGroup[] subcommandGroups = new SubcommandGroup[]{};

	protected SlashCommand() {}

	/**
	 * Returns an array of all {@link Subcommand}s this command contains.
	 *
	 * @return An {@link Subcommand}-array.
	 */
	@Nonnull
	public final Subcommand[] getSubcommands() {
		return subcommands;
	}

	/**
	 * Adds {@link Subcommand}s to this {@link SlashCommand}. 
	 *
	 * @param classes Instances of the {@link Subcommand}s to add.
	 */
	public final void addSubcommands(@Nonnull Subcommand... classes) {
		for (Subcommand subcommand : classes) {
			subcommand.parent = this;
		}
		this.subcommands = classes;
	}

	/**
	 * Returns an array of all {@link SubcommandGroup}s this command contains.
	 *
	 * @return An {@link SubcommandGroup}-array.
	 */
	@Nonnull
	public final SubcommandGroup[] getSubcommandGroups() {
		return subcommandGroups;
	}

	/**
	 * Adds {@link SubcommandGroup}s to this {@link SlashCommand}.
	 *
	 * @param groups Instances of the {@link SubcommandGroup} class.
	 */
	public final void addSubcommandGroups(@Nonnull SubcommandGroup... groups) {
		for (SubcommandGroup group : groups) {
			for (Subcommand subcommand : group.getSubcommands()) {
				subcommand.parent = this;
			}
		}
		this.subcommandGroups = groups;
	}

	@Override
	public void execute(@Nonnull SlashCommandInteractionEvent event) {}

	/**
	 * Gets the corresponding {@link Command JDA entity} for this command.
	 * If the command was not cached or queued before (e.g. when using sharding), this may return null.
	 *
	 * @return The {@link Command} corresponding to this class.
	 */
	@Nonnull
	public Command asCommand() {
		return InteractionHandler.getRetrievedCommands().get(getCommandData().getName());
	}

	/**
	 * Model class which represents a single Subcommand.
	 */
	public abstract static class Subcommand extends ApplicationCommand<SlashCommandInteractionEvent, SubcommandData> {
		private SlashCommand parent = null;

		/**
		 * Creates a default instance.
		 */
		public Subcommand() {}

		/**
		 * Gets the {@link SlashCommand parent} for this subcommand.
		 *
		 * @return The corresponding {@link SlashCommand}.
		 */
		@Nullable
		public SlashCommand getParent() {
			return parent;
		}

		/**
		 * Gets the corresponding {@link Command.Subcommand JDA-entity} for this subcommand.
		 * If the subcommand was not cached or queued before (e.g. when using sharding), this may return null.
		 *
		 * @return The {@link Command.Subcommand} corresponding to this class.
		 */
		@Nullable
		public Command.Subcommand asSubcommand() {
			Command cmd = parent.asCommand();
			List<Command.Subcommand> subcommands = new ArrayList<>(cmd.getSubcommands());
			cmd.getSubcommandGroups().forEach(g -> subcommands.addAll(g.getSubcommands()));
			return subcommands.stream()
					.filter(c -> c.getName().equals(getCommandData().getName()))
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

		private SubcommandGroup(@Nonnull SubcommandGroupData data, @Nonnull Subcommand... subcommands) {
			this.data = data;
			this.subcommands = subcommands;
		}

		/**
		 * Creates a new instance of the {@link SubcommandGroup} class.
		 *
		 * @param data        The {@link SubcommandGroupData} to use.
		 * @param subcommands An array of {@link Subcommand}s. This should NOT be empty!
		 * @return The {@link SubcommandGroup}.
		 */
		@Nonnull
		public static SubcommandGroup of(@Nonnull SubcommandGroupData data, @Nonnull Subcommand... subcommands) {
			if (subcommands.length == 0) {
				throw new IllegalArgumentException("Subcommands may not be empty!");
			}
			return new SubcommandGroup(data, subcommands);
		}

		/**
		 * Gets the {@link SubcommandGroupData}.
		 *
		 * @return The corresponding {@link SubcommandGroupData}.
		 */
		@Nonnull
		public SubcommandGroupData getData() {
			return data;
		}

		/**
		 * Gets the {@link Subcommand}.
		 *
		 * @return An array of {@link Subcommand}s.
		 */
		@Nonnull
		public Subcommand[] getSubcommands() {
			return subcommands;
		}
	}
}