package xyz.dynxsty.examples.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

public class CustomRequirementsCommand extends SlashCommand {

	public CustomRequirementsCommand() {
		setCommandData(Commands.slash("can-execute", "Can I execute this command?"));
		setCanExecuteFunction(event -> {
			boolean canExecute = event.getMember().getEffectiveName().contains("-");
			if (!canExecute) {
				event.reply("You can not execute this command.").queue();
			}
			return canExecute;
		});
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.reply("You can execute this command!").queue();
	}
}
