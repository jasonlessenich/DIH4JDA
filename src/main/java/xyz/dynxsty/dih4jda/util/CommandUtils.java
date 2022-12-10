package xyz.dynxsty.dih4jda.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.data.DataObject;
import xyz.dynxsty.dih4jda.DIH4JDALogger;
import xyz.dynxsty.dih4jda.interactions.commands.application.BaseApplicationCommand;
import xyz.dynxsty.dih4jda.interactions.commands.application.ContextCommand;
import xyz.dynxsty.dih4jda.interactions.commands.application.RegistrationType;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A utility class that contains some useful methods regarding command data.
 *
 * @since v1.3
 */
public class CommandUtils {

	private CommandUtils() {}

	/**
	 * Compares two {@link DataObject}.
	 *
	 * @param data	The {@link DataObject}.
	 * @param other The other {@link DataObject}.
	 * @return Whether both {@link DataObject} share the same properties.
	 * @since v1.6
	 */
	public static synchronized boolean equals(@Nonnull DataObject data, @Nonnull DataObject other) {
		return Arrays.equals(ArrayUtil.sortArrayFromDataObject(data), ArrayUtil.sortArrayFromDataObject(other));
	}

	/**
	 * Takes a {@link Command} object that wraps a {@link SlashCommandData} object and compares it to a
	 * {@link SlashCommand} object.
	 *
	 * @param cmd The {@link Command} that wraps a {@link SlashCommandData} object.
	 * @param data The {@link SlashCommand} to compare two.
	 * @return true if both are identical.
	 * @see CommandUtils#equals(DataObject, DataObject)
	 */
	public static boolean compareSlashCommands(@Nonnull Command cmd, @Nonnull SlashCommand data) {
		return equals(CommandData.fromCommand(cmd).toData(), data.getCommandData().toData());
	}

	/**
	 * Takes a {@link Command} object that wraps a context-command and compares it to a {@link ContextCommand} object.
	 *
	 * @param cmd The {@link Command} that wraps a context-command.
	 * @param data The {@link ContextCommand} to compare two.
	 * @return true if both are identical.
	 */
	public static boolean compareContextCommands(@Nonnull Command cmd, @Nonnull ContextCommand<?> data) {
		return equals(CommandData.fromCommand(cmd).toData(), data.getCommandData().toData());
	}

	/**
	 * Used to create one command name out of the SlashCommand, SlashSubCommandGroup and SlashSubCommand
	 *
	 * @param args the arguments as {@link String}s you want to join together.
	 * @return One combined string.
	 * @since v1.4
	 */
	@Nonnull
	public static String buildCommandPath(@Nonnull String... args) {
		return String.join(" ", args);
	}

	/**
	 * Builds a formatted string out of the given sets of CommandData.
	 *
	 * @param command A set of {@link ContextCommand}s.
	 * @param slash   A set of {@link SlashCommand}s.
	 * @return The formatted String.
	 * @since v1.5
	 */
	@Nonnull
	public static String getNames(@Nonnull Set<ContextCommand<?>> command, @Nonnull Set<SlashCommand> slash) {
		StringBuilder names = new StringBuilder();
		command.forEach(c -> names.append(", ").append(c.getCommandData().getName()));
		slash.forEach(c -> names.append(", /").append(c.getCommandData().getName()));
		return names.substring(2);
	}

	/**
	 * Removes all elements of the provided {@link Pair} which don't match the given {@link RegistrationType}.
	 *
	 * @param pair The {@link Pair}.
	 * @param type The {@link RegistrationType}.
	 * @return The modified {@link Pair}.
	 * @since v1.5.2
	 */
	@Nonnull
	public static Pair<Set<SlashCommand>, Set<ContextCommand<?>>> filterByType(@Nonnull Pair<Set<SlashCommand>, Set<ContextCommand<?>>> pair,
																				@Nonnull RegistrationType type) {
		return new Pair<>(
				pair.getFirst().stream().filter(c -> c.getRegistrationType().equals(type)).collect(Collectors.toSet()),
				pair.getSecond().stream().filter(c -> c.getRegistrationType().equals(type)).collect(Collectors.toSet())
		);
	}

	/**
	 * Checks if a command should be registered on a specific guild.
	 *
	 * @param guild The {@link Guild} to check.
	 * @param command The {@link BaseApplicationCommand} to check.
	 * @return true if the command should be registered.
	 * @since v1.7
	 */
	public static boolean shouldBeRegistered(@Nonnull Guild guild, @Nonnull BaseApplicationCommand<?, ?> command) {
		Long[] guildIds = command.getQueueableGuilds();
		if (guildIds.length == 0 || List.of(guildIds).contains(guild.getIdLong())) {
			return true;
		} else {
			DIH4JDALogger.error(DIH4JDALogger.Type.SLASH_COMMAND_SKIPPED, "Skipping registration of a command, for " +
							"guild %s.", guild.getName());
		}
		return false;
	}

	/**
	 * Gets the mention from a {@link SlashCommand}.
	 *
	 * @param command the {@link SlashCommand} you want the mention from.
	 * @return the mention as a {@link String}.
	 * @since v1.6
	 */
	@Nonnull
	public static String getAsMention(@Nonnull SlashCommand command) {
		Command entity = command.asCommand();
		return entity.getAsMention();
	}

	/**
	 * Gets the mention from a {@link SlashCommand.Subcommand}.
	 *
	 * @param command the {@link SlashCommand.Subcommand} you want the mention from.
	 * @return the mention as a {@link String}.
	 * @since v1.6
	 */
	@Nullable
	public static String getAsMention(@Nonnull SlashCommand.Subcommand command) {
		Command.Subcommand entity = command.asSubcommand();
		if (entity == null) {
			return null;
		}
		return entity.getAsMention();
	}
}
