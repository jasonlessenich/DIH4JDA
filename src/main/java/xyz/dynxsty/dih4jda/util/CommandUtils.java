package xyz.dynxsty.dih4jda.util;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.data.DataObject;
import xyz.dynxsty.dih4jda.interactions.commands.application.ContextCommand;
import xyz.dynxsty.dih4jda.interactions.commands.application.RegistrationType;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
	public static boolean equals(@Nonnull DataObject data, @Nonnull DataObject other) {
		//.toMap() function is necessary because the DataObject does not have a custom implementation of .equals()
		return data.toMap().equals(other.toMap());
	}

	/**
	 * Checks if the {@link Command} is equal to the given {@link CommandData}.
	 *
	 * @param command The {@link Command}.
	 * @param data    The {@link CommandData}.
	 * @return Whether the given Command originates from the given CommandData.
	 * @since v1.5
	 */
	public static boolean equals(@Nonnull Command command, Object data) {
		if (command.getType() == Command.Type.SLASH) {
			return CommandUtils.equals(((SlashCommandData) data).toData(), SlashCommandData.fromCommand(command).toData());
		} else {
			return CommandUtils.equals(((CommandData) data).toData(), CommandData.fromCommand(command).toData());
		}
	}

	/**
	 * Used to create one command name out of the SlashCommand, SlashSubCommandGroup and SlashSubCommand
	 *
	 * @param args the arguments as {@link String}s you want to join together.
	 * @return One combined string.
	 * @since v1.4
	 */
	public static @Nonnull String buildCommandPath(String... args) {
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
	public static @Nonnull String getNames(@Nonnull Set<ContextCommand<?>> command, @Nonnull Set<SlashCommand> slash) {
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
	public static @Nonnull Pair<Set<SlashCommand>, Set<ContextCommand<?>>> filterByType(@Nonnull Pair<Set<SlashCommand>,
			Set<ContextCommand<?>>> pair, RegistrationType type) {
		return new Pair<>(
				pair.getFirst().stream().filter(c -> c.getRegistrationType() == type).collect(Collectors.toSet()),
				pair.getSecond().stream().filter(c -> c.getRegistrationType() == type).collect(Collectors.toSet())
		);
	}

	/**
	 * Gets the mention from a {@link SlashCommand}.
	 *
	 * @param command the {@link SlashCommand} you want the mention from.
	 * @return the mention as a {@link String}.
	 */
	public static @Nullable String getAsMention(@Nonnull SlashCommand command) {
		Command entity = command.asCommand();
		if (entity == null) {
			return null;
		}
		return entity.getAsMention();
	}

	/**
	 * Gets the mention from a {@link SlashCommand.Subcommand}.
	 *
	 * @param command the {@link SlashCommand.Subcommand} you want the mention from.
	 * @return the mention as a {@link String}.
	 */
	public static @Nullable String getAsMention(@Nonnull SlashCommand.Subcommand command) {
		Command.Subcommand entity = command.asSubcommand();
		if (entity == null) {
			return null;
		}
		return entity.getAsMention();
	}
}
