package com.dynxsty.dih4jda.slash_command;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public interface ISlashCommand {
    void handleSlash(SlashCommandEvent event);
}