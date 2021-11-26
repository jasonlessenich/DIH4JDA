package com.dynxsty.dih4jda.slash_command;

import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

/**
 * @param handler The {@link ISlashCommand}.
 * @param privileges A array of {@link CommandPrivilege}'s
 */
public record SlashCommandInteraction(ISlashCommand handler, CommandPrivilege[] privileges) {
}
