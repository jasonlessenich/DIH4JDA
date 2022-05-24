package com.dyxnsty.bot.config;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;

public class GetCommand extends ConfigCommand {
	public GetCommand() {
		setCommandData(Commands.slash("get", "get")
				.addOption(OptionType.STRING, "value", "value", true));
	}

	@Override
	public WebhookMessageAction<Message> handle(SlashCommandInteractionEvent event) {
		return null;
	}
}
