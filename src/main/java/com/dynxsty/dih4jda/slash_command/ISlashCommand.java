package com.dynxsty.dih4jda.slash_command;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

/**
 * An interface for DIH4JDA's Slash Commands.
 */
public interface ISlashCommand {
    void handleSlash(SlashCommandEvent event);
}