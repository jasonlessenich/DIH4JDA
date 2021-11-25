package com.dynxsty.dih4jda.slash_command.dto;

import lombok.Data;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

@Data
public class SlashCommand {
    private CommandData commandData;
    private Class<? extends SlashSubCommand>[] subCommandClasses;
    private Class<? extends SlashSubCommandGroup>[] subCommandGroupClasses;
    private CommandPrivilege[] commandPrivileges;
}
