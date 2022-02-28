package com.dynxsty.dih4jda.commands.interactions.slash_command.dao;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

/**
 * A SlashSubCommandGroup object with getters, setters, a constructor and a toString method.
 */
public abstract class SlashSubcommandGroup {
    private SubcommandGroupData subcommandGroupData;
    private Class<? extends SlashSubcommand>[] subcommandClasses;

    public SubcommandGroupData getSubcommandGroupData() {
        return subcommandGroupData;
    }

    public void setSubcommandGroupData(SubcommandGroupData subcommandGroupData) {
        this.subcommandGroupData = subcommandGroupData;
    }

    public Class<? extends SlashSubcommand>[] getSubcommandClasses() {
        return subcommandClasses;
    }

    public void setSubcommandClasses(Class<? extends SlashSubcommand>... classes) {
        this.subcommandClasses = classes;
    }
}
