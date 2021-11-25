package com.dynxsty.dih4jda.slash_command;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;

public interface ISlashCommand {
    WebhookMessageAction<Message> handleSlash(SlashCommandEvent event);
}