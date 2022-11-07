package xyz.dynxsty.dih4jda.util;

import xyz.dynxsty.dih4jda.interactions.commands.ContextCommand;
import xyz.dynxsty.dih4jda.interactions.commands.RegistrationType;
import xyz.dynxsty.dih4jda.interactions.commands.SlashCommand;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.Contract;

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
	 * Compares two {@link SlashCommandData} (Slash Command) objects.
	 *
	 * @param data    The {@link SlashCommandData}
	 * @param command The other {@link SlashCommandData} object.
	 * @return Whether both {@link SlashCommandData} objects share the same properties.
	 * @since v1.5
	 */
	public static boolean equals(@Nonnull SlashCommandData data, @Nonnull SlashCommandData command, boolean isGlobalCommand) {
		if (data.getType() != command.getType()) return false;
		if (!data.getName().equals(command.getName())) return false;
		if (!data.getDescription().equals(command.getDescription())) return false;
		if (isGlobalCommand && (data.isGuildOnly() != command.isGuildOnly())) return false;
		if (!equals(data.getDefaultPermissions(), command.getDefaultPermissions())) return false;
		if (!data.getOptions().stream().allMatch(o -> command.getOptions().stream().anyMatch(op -> equals(o, op)))) {
			return false;
		}
		if (!data.getSubcommandGroups().stream().allMatch(o -> command.getSubcommandGroups().stream().anyMatch(op -> equals(o, op)))) {
			return false;
		}
		if (!data.getSubcommands().stream().allMatch(o -> command.getSubcommands().stream().anyMatch(op -> equals(o, op)))) {
			return false;
		}
		return data.getSubcommands().stream().allMatch(o -> command.getSubcommands().stream().anyMatch(op -> equals(o, op)));
	}

	/**
	 * Compares two {@link CommandData} (Context Command) objects.
	 *
	 * @param data    The {@link CommandData}
	 * @param command The other {@link CommandData} object.
	 * @return Whether both {@link CommandData} objects share the same properties.
	 * @since v1.5
	 */
	public static boolean equals(@Nonnull CommandData data, @Nonnull CommandData command, boolean isGlobalCommand) {
		if (data.getType() != command.getType()) return false;
		if (isGlobalCommand && (data.isGuildOnly() != command.isGuildOnly())) return false;
		if (!equals(data.getDefaultPermissions(), command.getDefaultPermissions())) return false;
		return data.getName().equals(command.getName());
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
	 * Compares two {@link SubcommandData} objects.
	 *
	 * @param data       The {@link SubcommandData}
	 * @param subcommand The other {@link SubcommandData} object.
	 * @return Whether both {@link SubcommandData} objects share the same properties.
	 * @since v1.5
	 */
	public static boolean equals(@Nonnull SubcommandData data, @Nonnull SubcommandData subcommand) {
		if (!data.getName().equals(subcommand.getName())) return false;
		if (!data.getDescription().equals(subcommand.getDescription())) return false;
		return data.getOptions().stream().allMatch(o -> subcommand.getOptions().stream().anyMatch(op -> equals(o, op)));
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
		if (!data.getName().equals(group.getName())) return false;
		if (!data.getDescription().equals(group.getDescription())) return false;
		return data.getSubcommands().stream().allMatch(o -> group.getSubcommands().stream().anyMatch(op -> equals(o, op)));
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
		if (data.getType() != option.getType()) return false;
		if (!data.getName().equals(option.getName())) return false;
		if (!data.getDescription().equals(option.getDescription())) return false;
		if (!data.getChoices().equals(option.getChoices())) return false;
		if (!data.getChannelTypes().equals(option.getChannelTypes())) return false;
		if (!Objects.equals(data.getMaxValue(), option.getMaxValue())) return false;
		if (!Objects.equals(data.getMinValue(), option.getMinValue())) return false;
		if (data.isAutoComplete() != option.isAutoComplete()) return false;
		return data.isRequired() == option.isRequired();
	}

	/**
	 * Checks if the {@link Command} is equal to the given {@link CommandData}.
	 *
	 * @param command The {@link Command}.
	 * @param data    The {@link CommandData}.
	 * @return Whether the given Command originates from the given CommandData.
	 * @since v1.5
	 */
	public static boolean isEqual(Command command, Object data, boolean isGlobalCommand) {
		boolean equals;
		if (command.getType() == Command.Type.SLASH) {
			equals = CommandUtils.equals((SlashCommandData) data, SlashCommandData.fromCommand(command), isGlobalCommand);
		} else {
			equals = CommandUtils.equals((CommandData) data, CommandData.fromCommand(command), isGlobalCommand);
		}
		return equals;
	}

	/**
	 * Used to create one command name out of the SlashCommand, SlashSubCommandGroup and SlashSubCommand
	 *
	 * @return One combined string.
	 * @since v1.4
	 */
	@Contract(pure = true)
	public static @Nonnull String buildCommandPath(String... args) {
		return String.join("/", args);
	}

	/**
	 * Builds a formatted string out of the given sets of CommandData.
	 *
	 * @param command A set of {@link ContextCommand}s.
	 * @param slash   A set of {@link SlashCommand}s.
	 * @return The formatted String.
	 * @since v1.5
	 */
	public static @Nonnull String getNames(@NotNull Set<ContextCommand> command, @NotNull Set<SlashCommand> slash) {
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
	@Contract("_, _ -> new")
	public static @Nonnull Pair<Set<SlashCommand>, Set<ContextCommand>> filterByType(@NotNull Pair<Set<SlashCommand>,
			Set<ContextCommand>> pair, RegistrationType type) {
		return new Pair<>(
				pair.getFirst().stream().filter(c -> c.getRegistrationType() == type).collect(Collectors.toSet()),
				pair.getSecond().stream().filter(c -> c.getRegistrationType() == type).collect(Collectors.toSet()));
	}
}
