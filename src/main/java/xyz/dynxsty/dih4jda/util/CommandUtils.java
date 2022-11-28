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
	//synchronized -> because DataObject is not thread safe!
	public static synchronized boolean equals(@Nonnull DataObject data, @Nonnull DataObject other) {
		boolean bo = Arrays.equals(ArrayUtil.getSortedArrayFromDataObject(data),
				ArrayUtil.getSortedArrayFromDataObject(other));
		//.toMap() function is necessary because the DataObject does not have a custom implementation of .equals()
		System.out.println(data.toMap());
		System.out.println("\n");
		System.out.println(other.toMap());
		System.out.println("Equals: " + bo + "\n\n");
		return bo;
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
	public static String getNames(@Nonnull List<ContextCommand<?>> command, @Nonnull List<SlashCommand> slash) {
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
	public static Pair<List<SlashCommand>, List<ContextCommand<?>>> filterByType(@Nonnull Pair<Set<SlashCommand>, Set<ContextCommand<?>>> pair,
																				@Nonnull RegistrationType type) {
		return new Pair<>(
				pair.getFirst().stream().filter(c -> c.getRegistrationType().equals(type)).collect(Collectors.toList()),
				pair.getSecond().stream().filter(c -> c.getRegistrationType().equals(type)).collect(Collectors.toList())
		);
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
