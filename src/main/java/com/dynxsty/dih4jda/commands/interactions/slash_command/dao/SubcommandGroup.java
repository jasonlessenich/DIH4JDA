package com.dynxsty.dih4jda.commands.interactions.slash_command.dao;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

/**
 * A SlashSubCommandGroup object with getters, setters, a constructor and a toString method.
 */
public abstract class SubcommandGroup {
    private SubcommandGroupData subcommandGroupData;
    private Class<? extends Subcommand>[] subcommandClasses;

    public SubcommandGroupData getSubcommandGroupData() {
        return subcommandGroupData;
    }

    public void setSubcommandGroupData(SubcommandGroupData subcommandGroupData) {
        this.subcommandGroupData = subcommandGroupData;
    }

    public Class<? extends Subcommand>[] getSubcommands() {
        return subcommandClasses;
    }

    public void setSubcommands(Class<? extends Subcommand>... classes) {
        this.subcommandClasses = classes;
    }
}
