package com.dynxsty.dih4jda.interactions.commands.slash_command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

//TODO-v1.4: Documentation
public interface SlashCommand {
    void handleSlashCommand(SlashCommandInteractionEvent event);
}