package com.dynxsty.dih4jda.commands;

import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

/**
 * @param handler The {@link ISlashCommand}.
 * @param privileges An array of {@link CommandPrivilege}'s
 */
public record SlashCommandInteraction(ISlashCommand handler, CommandPrivilege[] privileges) {
}
