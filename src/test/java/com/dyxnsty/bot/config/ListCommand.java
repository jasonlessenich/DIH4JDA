package com.dyxnsty.bot.config;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;

public class ListCommand extends ConfigCommand {
	public ListCommand() {
		setCommandData(Commands.slash("list", "list"));
	}

	@Override
	public WebhookMessageAction<Message> handle(SlashCommandInteractionEvent event) {
		return null;
	}
}
