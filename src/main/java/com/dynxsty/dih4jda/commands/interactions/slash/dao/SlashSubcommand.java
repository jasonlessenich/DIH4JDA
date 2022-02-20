package com.dynxsty.dih4jda.commands.interactions.slash.dao;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * A SlashSubCommand object with getters, setters, a constructor and a toString method.
 */
public abstract class SlashSubcommand {
    private SubcommandData subcommandData;

    public SubcommandData getSubcommandData() {
        return subcommandData;
    }

    public void setSubcommandData(SubcommandData subCommandData) {
        this.subcommandData = subCommandData;
    }
}
