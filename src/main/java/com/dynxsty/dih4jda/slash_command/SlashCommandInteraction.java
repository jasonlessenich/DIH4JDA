package com.dynxsty.dih4jda.slash_command;

import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

public record SlashCommandInteraction(ISlashCommand handler, CommandPrivilege[] privileges) {
}
