package com.dynxsty.dih4jda.commands.dto;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * A SlashSubCommand object with getters, setters, a constructor and a toString method.
 */
public abstract class SlashSubcommand {
    private SubcommandData subCommandData;

    public SubcommandData getSubCommandData() {
        return subCommandData;
    }

    public void setSubCommandData(SubcommandData subCommandData) {
        this.subCommandData = subCommandData;
    }
}
