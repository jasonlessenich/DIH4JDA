package com.dynxsty.dih4jda.slash_command.dto;

import lombok.Data;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

@Data
public class SlashSubCommandGroup {
    protected SubcommandGroupData subCommandGroupData;
    protected Class<? extends SlashSubCommand>[] subCommandClasses;
}
