package com.dyxnsty.bot.config;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;

public class SetCommand extends ConfigCommand {
	public SetCommand() {
		setCommandData(Commands.slash("set", "set")
				.addOption(OptionType.STRING, "value", "value", true));
	}

	@Override
	public WebhookMessageAction<Message> handle(SlashCommandInteractionEvent event) {
		return null;
	}
}
