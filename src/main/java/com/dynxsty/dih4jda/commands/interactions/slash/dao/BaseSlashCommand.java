package com.dynxsty.dih4jda.commands.interactions.slash.dao;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

/**
 * A SlashCommand object with getters, setters, a constructor and a toString method.
 */
public abstract class BaseSlashCommand {
    protected BaseSlashCommand() {}

    private SlashCommandData commandData;
    private Class<? extends SlashSubcommand>[] subcommandClasses;
    private Class<? extends SlashSubcommandGroup>[] subcommandGroupClasses;
    private CommandPrivilege[] commandPrivileges;

    public SlashCommandData getCommandData() {
        return commandData;
    }

    public void setCommandData(SlashCommandData commandData) {
        this.commandData = commandData;
    }

    public Class<? extends SlashSubcommand>[] getSubcommandClasses() {
        return subcommandClasses;
    }

    @SafeVarargs
    public final void setSubcommandClasses(Class<? extends SlashSubcommand>... classes) {
        this.subcommandClasses = classes;
    }

    public Class<? extends SlashSubcommandGroup>[] getSubcommandGroupClasses() {
        return subcommandGroupClasses;
    }

    @SafeVarargs
    public final void setSubcommandGroupClasses(Class<? extends SlashSubcommandGroup>... classes) {
        this.subcommandGroupClasses = classes;
    }

    public CommandPrivilege[] getCommandPrivileges() {
        return commandPrivileges;
    }

    public void setCommandPrivileges(CommandPrivilege... commandPrivileges) {
        this.commandPrivileges = commandPrivileges;
    }
}