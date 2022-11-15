package xyz.dynxsty.dih4jda.util;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationMap;
import xyz.dynxsty.dih4jda.interactions.commands.application.ContextCommand;
import xyz.dynxsty.dih4jda.interactions.commands.application.RegistrationType;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
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
	 * Compares two {@link SlashCommandData} (Slash Command) objects.
	 *
	 * @param data    The {@link SlashCommandData}
	 * @param command The other {@link SlashCommandData} object.
	 * @param isGlobalCommand a {@link Boolean} that is ture if the provided commands are global commands else false.
	 * @return Whether both {@link SlashCommandData} objects share the same properties.
	 * @since v1.5
	 */
	public static boolean equals(@Nonnull SlashCommandData data, @Nonnull SlashCommandData command, boolean isGlobalCommand) {
		return data.toData().toMap().equals(command.toData().toMap());
	}

	/**
	 * Compares two {@link CommandData} (Context Command) objects.
	 *
	 * @param data    The {@link CommandData}
	 * @param command The other {@link CommandData} object.
	 * @param isGlobalCommand a {@link Boolean} that is ture if the provided commands are global commands else false.
	 * @return Whether both {@link CommandData} objects share the same properties.
	 * @since v1.5
	 */
	public static boolean equals(@Nonnull CommandData data, @Nonnull CommandData command, boolean isGlobalCommand) {
		return data.toData().toMap().equals(command.toData().toMap());
	}

	/**
	 * Compares two {@link DefaultMemberPermissions} objects.
	 *
	 * @param data The {@link DefaultMemberPermissions}.
	 * @param command The other {@link DefaultMemberPermissions} object.
	 * @return Whether both {@link DefaultMemberPermissions} objects are equal.
	 * @since v1.5.5
	 */
	public static boolean equals(@Nonnull DefaultMemberPermissions data, @Nonnull DefaultMemberPermissions command) {
		return Objects.equals(data.getPermissionsRaw(), command.getPermissionsRaw());
	}

	/**
	 * Compares to {@link LocalizationMap} objects.
	 *
	 * @param data The {@link LocalizationMap}.
	 * @param command The other {@link LocalizationMap} object.
	 * @return Whether both {@link LocalizationMap} objects are equal.
	 * @since v1.6
	 */
	public static boolean equals(@Nonnull LocalizationMap data, @Nonnull LocalizationMap command) {
		return data.toMap().equals(command.toMap());
	}

	/**
	 * Compares two {@link SubcommandData} objects.
	 *
	 * @param data       The {@link SubcommandData}
	 * @param subcommand The other {@link SubcommandData} object.
	 * @return Whether both {@link SubcommandData} objects share the same properties.
	 * @since v1.5
	 */
	public static boolean equals(@Nonnull SubcommandData data, @Nonnull SubcommandData subcommand) {
		return data.toData().toMap().equals(subcommand.toData().toMap());
	}

	/**
	 * Compares two {@link SubcommandGroupData} objects.
	 *
	 * @param data  The {@link SubcommandGroupData}
	 * @param group The other {@link SubcommandGroupData} object.
	 * @return Whether both {@link SubcommandGroupData} objects share the same properties.
	 * @since v1.5
	 */
	public static boolean equals(@Nonnull SubcommandGroupData data, @Nonnull SubcommandGroupData group) {
		return data.toData().toMap().equals(group.toData().toMap());
	}

	/**
	 * Compares two {@link OptionData} objects.
	 *
	 * @param data   The {@link OptionData}
	 * @param option The other {@link OptionData} object.
	 * @return Whether both {@link OptionData} objects share the same properties.
	 * @since v1.5
	 */
	public static boolean equals(@Nonnull OptionData data, @Nonnull OptionData option) {
		return data.toData().toMap().equals(option.toData().toMap());
	}

	/**
	 * Checks if the {@link Command} is equal to the given {@link CommandData}.
	 *
	 * @param command The {@link Command}.
	 * @param data    The {@link CommandData}.
	 * @param isGlobalCommand a {@link Boolean} that is ture if the provided commands are global commands else false.
	 * @return Whether the given Command originates from the given CommandData.
	 * @since v1.5
	 */
	public static boolean equals(@Nonnull Command command, Object data, boolean isGlobalCommand) {
		if (command.getType() == Command.Type.SLASH) {
			return CommandUtils.equals((SlashCommandData) data, SlashCommandData.fromCommand(command), isGlobalCommand);
		} else {
			return CommandUtils.equals((CommandData) data, CommandData.fromCommand(command), isGlobalCommand);
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
