package xyz.dynxsty.dih4jda.util;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.Contract;
import xyz.dynxsty.dih4jda.interactions.commands.ContextCommand;
import xyz.dynxsty.dih4jda.interactions.commands.RegistrationType;
import xyz.dynxsty.dih4jda.interactions.commands.SlashCommand;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A utility class that contains some useful methods regarding command data.
 *
 * @since v1.3
 */
public class CommandUtils {

	private CommandUtils() {
	}

	/**
	 * Used to create one command name out of the SlashCommand, SlashSubCommandGroup and SlashSubCommand
	 *
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
	public static @Nonnull String getNames(@Nonnull Set<ContextCommand> command, @Nonnull Set<SlashCommand> slash) {
		StringBuilder names = new StringBuilder();
		command.forEach(c -> names.append(", ").append(c.getCommandData().getName()));
		slash.forEach(c -> names.append(", /").append(c.getSlashCommandData().getName()));
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
	public static @Nonnull Pair<Set<SlashCommand>, Set<ContextCommand>> filterByType(@Nonnull Pair<Set<SlashCommand>,
			Set<ContextCommand>> pair, RegistrationType type) {
		return new Pair<>(
				pair.getFirst().stream().filter(c -> c.getRegistrationType() == type).collect(Collectors.toSet()),
				pair.getSecond().stream().filter(c -> c.getRegistrationType() == type).collect(Collectors.toSet()));
	}
}
