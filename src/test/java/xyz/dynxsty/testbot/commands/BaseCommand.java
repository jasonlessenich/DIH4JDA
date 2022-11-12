package xyz.dynxsty.testbot.commands;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

public class BaseCommand extends SlashCommand {
	public BaseCommand() {
		setCommandData(Commands.slash("base", "dummy"));
		addSubcommands(new Subcommand1());
		addSubcommandGroups(SubcommandGroup.of(new SubcommandGroupData("group", "dummy"), new Subcommand2()));
	}
}
