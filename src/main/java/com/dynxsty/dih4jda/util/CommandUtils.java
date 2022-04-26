package com.dynxsty.dih4jda.util;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.List;
import java.util.stream.Collectors;

public class CommandUtils {

	/**
	 * Compares a {@link SlashCommandData} (Slash Command) and a {@link Command} object.
	 *
	 * @param data    The {@link SlashCommandData}
	 * @param command The {@link Command} object.
	 * @return Whether the {@link Command} object has the same properties as the {@link CommandData}. Assuming they're both the same.
	 */
	public static boolean equals(SlashCommandData data, Command command) {
		if (command.getType() != Command.Type.SLASH) return false;
		SlashCommandData commandData = CommandUtils.toSlashCommandData(command);
		if (!data.getName().equals(commandData.getName())) return false;
		if (!data.getDescription().equals(commandData.getDescription())) return false;
		if (!data.getOptions().equals(commandData.getOptions())) return false;
		if (!data.getSubcommandGroups().equals(commandData.getSubcommandGroups())) return false;
		if (!data.getSubcommandGroups().stream().map(SubcommandGroupData::getSubcommands).collect(Collectors.toList()).equals(
				commandData.getSubcommandGroups().stream().map(SubcommandGroupData::getSubcommands).collect(Collectors.toList())))
			return false;
		return data.getSubcommands().equals(commandData.getSubcommands());
	}

	/**
	 * Compares a {@link CommandData} (Context Command) and a {@link Command} object.
	 *
	 * @param data    The {@link CommandData}
	 * @param command The {@link Command} object.
	 * @return Whether the {@link Command} object has the same properties as the {@link CommandData}. Assuming they're both the same.
	 */
	public static boolean equals(CommandData data, Command command) {
		if (command.getType() != Command.Type.MESSAGE && command.getType() != Command.Type.USER) return false;
		CommandData commandData = CommandUtils.toCommandData(command);
		if (!data.getName().equals(commandData.getName())) return false;
		return data.getType() == command.getType();
	}

	/**
	 * Converts a {@link Command} object into a similar {@link SlashCommandData}.
	 *
	 * @param command The {@link Command} object.
	 * @return The recreated {@link SlashCommandData}.
	 */
	public static SlashCommandData toSlashCommandData(Command command) {
		if (command.getType() != Command.Type.SLASH) throw new IllegalArgumentException("Command is not of Type SLASH");
		SlashCommandData data = Commands.slash(command.getName(), command.getDescription())
				.addOptions(CommandUtils.toOptionData(command.getOptions()))
				.setDefaultEnabled(command.isDefaultEnabled());
		data.addSubcommandGroups(command.getSubcommandGroups()
				.stream()
				.map(s -> new SubcommandGroupData(s.getName(), s.getDescription()).addSubcommands(
						s.getSubcommands().stream()
								.map(c -> new SubcommandData(c.getName(), c.getDescription()))
								.collect(Collectors.toList())
				)).collect(Collectors.toList()));
		data.addSubcommands(command.getSubcommands()
				.stream()
				.map(s -> new SubcommandData(s.getName(), s.getDescription())
						.addOptions(CommandUtils.toOptionData(s.getOptions()))
				).collect(Collectors.toList()));
		return data;
	}

	/**
	 * Converts a List of {@link Command.Option}s to a List of {@link OptionData}.
	 *
	 * @param options The list of {@link Command.Option}s.
	 * @return The List with all {@link OptionData}.
	 */
	public static List<OptionData> toOptionData(List<Command.Option> options) {
		return options.stream()
				.map(o -> new OptionData(o.getType(), o.getName(), o.getDescription(), o.isRequired(), o.isAutoComplete()))
				.collect(Collectors.toList());
	}

	/**
	 * Converts a {@link Command} object into a similar {@link CommandData}.
	 *
	 * @param command The {@link Command} object.
	 * @return The recreated {@link CommandData}.
	 */
	public static CommandData toCommandData(Command command) {
		if (command.getType() != Command.Type.MESSAGE && command.getType() != Command.Type.USER) throw new IllegalArgumentException("Command is not of Type CONTEXT");
		return Commands.context(command.getType(), command.getName())
				.setDefaultEnabled(command.isDefaultEnabled());
	}
}
