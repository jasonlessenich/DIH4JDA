package com.dyxnsty.bot.config;

import com.dynxsty.dih4jda.interactions.commands.SlashCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;

public abstract class ConfigCommand extends SlashCommand {
	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply().queue();
		handle(event).queue();
	}

	public abstract WebhookMessageAction<Message> handle(SlashCommandInteractionEvent event);
}
