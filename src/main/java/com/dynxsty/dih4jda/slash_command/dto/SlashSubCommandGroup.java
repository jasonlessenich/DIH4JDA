package com.dynxsty.dih4jda.slash_command.dto;

import lombok.Data;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

/**
 * A SlashSubCommandGroup object with getters, setters, a constructor and a toString method. Uses the {@link Data} annotation.
 */
@Data
public class SlashSubCommandGroup {
    protected SubcommandGroupData subCommandGroupData;
    protected Class<? extends SlashSubCommand>[] subCommandClasses;
}
