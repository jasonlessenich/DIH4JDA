package com.dynxsty.dih4jda.util;

import com.dynxsty.dih4jda.interactions.commands.ExecutableCommand;
import com.dynxsty.dih4jda.interactions.commands.model.UnqueuedCommandData;
import com.dynxsty.dih4jda.interactions.commands.model.UnqueuedSlashCommandData;
import net.dv8tion.jda.api.interactions.commands.CommandPermissions;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandUtils {

	private CommandUtils() {
	}

	/**
	 * Compares two {@link SlashCommandData} (Slash Command) objects.
	 *
	 * @param data    The {@link SlashCommandData}
	 * @param command The other {@link SlashCommandData} object.
	 * @return Whether both {@link SlashCommandData} objects share the same properties.
	 */
	public static boolean equals(@NotNull SlashCommandData data, @NotNull SlashCommandData command) {
		if (data.getType() != command.getType()) return false;
		if (!data.getName().equals(command.getName())) return false;
		if (!data.getDescription().equals(command.getDescription())) return false;
		if (data.isGuildOnly() != command.isGuildOnly()) return false;
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
	 */
	public static boolean equals(@NotNull CommandData data, @NotNull CommandData command) {
		if (data.getType() != command.getType()) return false;
		if (data.isGuildOnly() != command.isGuildOnly()) return false;
		if (!equals(data.getDefaultPermissions(), command.getDefaultPermissions())) return false;
		return data.getName().equals(command.getName());
	}

	/**
	 * Compares two {@link CommandPermissions} objects.
	 *
	 * @param data The {@link CommandPermissions}.
	 * @param command The other {@link CommandPermissions} object.
	 * @return Whether both {@link CommandPermissions} objects are equal.
	 */
	public static boolean equals(@NotNull CommandPermissions data, @NotNull CommandPermissions command) {
		return Objects.equals(data.getPermissionsRaw(), command.getPermissionsRaw());
	}

	/**
	 * Compares two {@link SubcommandData} objects.
	 *
	 * @param data       The {@link SubcommandData}
	 * @param subcommand The other {@link SubcommandData} object.
	 * @return Whether both {@link SubcommandData} objects share the same properties.
	 */
	public static boolean equals(@NotNull SubcommandData data, @NotNull SubcommandData subcommand) {
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
	 */
	public static boolean equals(@NotNull SubcommandGroupData data, @NotNull SubcommandGroupData group) {
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
	 */
	public static boolean equals(@NotNull OptionData data, @NotNull OptionData option) {
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
	 * Used to create one command name out of the SlashCommand, SlashSubCommandGroup and SlashSubCommand
	 *
	 * @return One combined string.
	 */
	@Contract(pure = true)
	public static @NotNull String buildCommandPath(String... args) {
		return String.join("/", args);
	}

	/**
	 * Builds a formatted string out of the given sets of CommandData.
	 *
	 * @param command A set of {@link UnqueuedCommandData}.
	 * @param slash   A set of {@link UnqueuedSlashCommandData}.
	 * @return The formatted String.
	 */
	public static String getNames(Set<UnqueuedCommandData> command, Set<UnqueuedSlashCommandData> slash) {
		StringBuilder names = new StringBuilder();
		command.forEach(c -> names.append(", ").append(c.getData().getName()));
		slash.forEach(c -> names.append(", /").append(c.getData().getName()));
		return names.substring(2);
	}

	/**
	 * Removes all elements of the provided {@link Pair} which don't match the given {@link ExecutableCommand.Type}.
	 *
	 * @param pair The {@link Pair}.
	 * @param type The {@link ExecutableCommand.Type}.
	 * @return The modified {@link Pair}.
	 */
	public static Pair<Set<UnqueuedSlashCommandData>, Set<UnqueuedCommandData>> filterByType(Pair<Set<UnqueuedSlashCommandData>,
			Set<UnqueuedCommandData>> pair, ExecutableCommand.Type type) {
		return new Pair<>(
				pair.getFirst().stream().filter(c -> c.getType() == type).collect(Collectors.toSet()),
				pair.getSecond().stream().filter(c -> c.getType() == type).collect(Collectors.toSet()));
	}
}
