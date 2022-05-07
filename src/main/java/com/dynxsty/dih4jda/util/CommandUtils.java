package com.dynxsty.dih4jda.util;

import com.dynxsty.dih4jda.DIH4JDALogger;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandUtils {

	/**
	 * Compares a {@link SlashCommandData} (Slash Command) and a {@link Command} object.
	 *
	 * @param data    The {@link SlashCommandData}
	 * @param command The other {@link SlashCommandData} object.
	 * @return Whether the {@link Command} object has the same properties as the {@link CommandData}. Assuming they're both the same.
	 */
	public static boolean equals(SlashCommandData data, SlashCommandData command) {
		if (data.getType() != command.getType()) return false;
		if (!data.getName().equals(command.getName())) return false;
		if (!data.getDescription().equals(command.getDescription())) return false;
		if (!command.getOptions().stream().allMatch(o -> data.getOptions().stream().anyMatch(op -> equals(o, op)))) return false;
		if (!command.getSubcommandGroups().stream().allMatch(o -> data.getSubcommandGroups().stream().anyMatch(op -> equals(o, op)))) return false;
		if (!command.getSubcommands().stream().allMatch(o -> data.getSubcommands().stream().anyMatch(op -> equals(o, op)))) return false;
		return command.getSubcommands().stream().allMatch(o -> data.getSubcommands().stream().anyMatch(op -> equals(o, op)));
	}

	/**
	 * Compares a {@link CommandData} (Context Command) and a {@link Command} object.
	 *
	 * @param data    The {@link CommandData}
	 * @param command The {@link Command} object.
	 * @return Whether the {@link Command} object has the same properties as the {@link CommandData}. Assuming they're both the same.
	 */
	public static boolean equals(CommandData data, CommandData command) {
		if (data.getType() != command.getType()) return false;
		return data.getName().equals(command.getName());
	}

	// TODO v1.5: Documentation
	public static boolean equals(SubcommandData data, SubcommandData command) {
		if (!data.getName().equals(command.getName())) return false;
		if (!data.getDescription().equals(command.getDescription())) return false;
		return data.getOptions().stream().allMatch(o -> command.getOptions().stream().anyMatch(op -> equals(o, op)));
	}

	// TODO v1.5: Documentation
	public static boolean equals(SubcommandGroupData data, SubcommandGroupData group) {
		if (!data.getName().equals(group.getName())) return false;
		if (!data.getDescription().equals(group.getDescription())) return false;
		return data.getSubcommands().stream().allMatch(o -> group.getSubcommands().stream().anyMatch(op -> equals(o, op)));
	}

	// TODO v1.5: Documentation
	public static boolean equals(OptionData data, OptionData option) {
		if (data.getType() != option.getType()) return false;
		if (!data.getName().equals(option.getName())) return false;
		if (!data.getDescription().equals(option.getDescription())) return false;
		if (!data.getChoices().equals(option.getChoices())) return false;
		if (!data.getChannelTypes().equals(option.getChannelTypes())) return false;
		if (!Objects.equals(data.getMaxValue(), option.getMaxValue())) return false;
		return Objects.equals(data.getMinValue(), option.getMinValue());
	}

	/**
	 * Converts a {@link Command} object into a similar {@link SlashCommandData}.
	 *
	 * @param command The {@link Command} object.
	 * @return The recreated {@link SlashCommandData}.
	 */
	public static SlashCommandData toSlashCommandData(Command command) {
		if (command.getType() != Command.Type.SLASH) throw new IllegalArgumentException("Command is not of Type SLASH");
		return Commands.slash(command.getName(), command.getDescription())
				.addOptions(CommandUtils.toOptionData(command.getOptions()))
				.setDefaultEnabled(command.isDefaultEnabled())
				.addSubcommands(toSubcommandData(command.getSubcommands()))
				.addSubcommandGroups(toSubcommandGroupData(command.getSubcommandGroups()));
	}

	// TODO v1.5: Documentation
	public static List<SubcommandData> toSubcommandData(List<Command.Subcommand> subcommands) {
		return subcommands.stream()
				.map(o -> {
					SubcommandData data = new SubcommandData(o.getName(), o.getDescription());
					if (!o.getOptions().isEmpty()) data.addOptions(toOptionData(o.getOptions()));
					return data;
				}).collect(Collectors.toList());
	}

	// TODO v1.5: Documentation
	public static List<SubcommandGroupData> toSubcommandGroupData(List<Command.SubcommandGroup> groups) {
		return groups.stream()
				.map(o -> {
					SubcommandGroupData data = new SubcommandGroupData(o.getName(), o.getDescription());
					if (!o.getSubcommands().isEmpty()) data.addSubcommands(toSubcommandData(o.getSubcommands()));
					return data;
				}).collect(Collectors.toList());
	}

	/**
	 * Converts a List of {@link Command.Option}s to a List of {@link OptionData}.
	 *
	 * @param options The list of {@link Command.Option}s.
	 * @return The List with all {@link OptionData}.
	 */
	public static List<OptionData> toOptionData(List<Command.Option> options) {
		return options.stream()
				.map(o -> {
					OptionData data = new OptionData(o.getType(), o.getName(), o.getDescription(), o.isRequired(), o.isAutoComplete());
					if (o.getMaxValue() != null) {
						data.setMaxValue(o.getMaxValue().getClass().isInstance(Long.class) ? o.getMaxValue().longValue() : o.getMaxValue().doubleValue());
					}
					if (o.getMinValue() != null) {
						data.setMinValue(o.getMinValue().getClass().isInstance(Long.class) ? o.getMinValue().longValue() : o.getMinValue().doubleValue());
					}
					if (!o.getChoices().isEmpty()) data.addChoices(o.getChoices());
					if (!o.getChannelTypes().isEmpty()) data.setChannelTypes(o.getChannelTypes());
					return data;
				}).collect(Collectors.toList());
	}

	/**
	 * Converts a {@link Command} object into a similar {@link CommandData}.
	 *
	 * @param command The {@link Command} object.
	 * @return The recreated {@link CommandData}.
	 */
	public static CommandData toCommandData(Command command) {
		if (command.getType() != Command.Type.MESSAGE && command.getType() != Command.Type.USER) {
			throw new IllegalArgumentException("Command is not of Type CONTEXT");
		}
		return Commands.context(command.getType(), command.getName())
				.setDefaultEnabled(command.isDefaultEnabled());
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

	// TODO v1.5: Documentation
	public static boolean isEqual(Command command, Object data) {
		boolean equals;
		if (command.getType() == Command.Type.SLASH) {
			equals = CommandUtils.equals((SlashCommandData) data, CommandUtils.toSlashCommandData(command));
		} else {
			equals = CommandUtils.equals((CommandData) data, CommandUtils.toCommandData(command));
		}
		if (equals) {
			DIH4JDALogger.info(String.format("Found duplicate %s command, which will be ignored: %s", command.getType(), command.getName()), DIH4JDALogger.Type.SMART_QUEUE);
		}
		return equals;
	}

	// TODO v1.5: Documentation
	public static String getNames(Set<CommandData> command, Set<SlashCommandData> slash) {
		StringBuilder names = new StringBuilder();
		command.forEach(c -> names.append(", ").append(c.getName()));
		slash.forEach(c -> names.append(", /").append(c.getName()));
		return names.substring(2);
	}

}
