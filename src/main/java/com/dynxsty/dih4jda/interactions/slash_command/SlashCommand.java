package com.dynxsty.dih4jda.interactions.slash_command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * An interface for DIH4JDA's Slash Commands.
 */
public interface SlashCommand {
    void handleSlashCommand(SlashCommandInteractionEvent event);
}