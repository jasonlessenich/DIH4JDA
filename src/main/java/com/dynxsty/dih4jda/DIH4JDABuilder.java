package com.dynxsty.dih4jda;

import net.dv8tion.jda.api.JDA;
import org.reflections.util.ClasspathHelper;

public class DIH4JDABuilder {

    private String ownerId, commandsPackage;
    private JDA jda;
    private SlashCommandType commandType;

    public DIH4JDABuilder setJDA(JDA instance) {
        jda = instance;
        return this;
    }

    public DIH4JDABuilder setOwnerId(String id) {
        ownerId = id;
        return this;
    }

    public DIH4JDABuilder setCommandsPackage(String pack) {
        commandsPackage = pack;
        return this;
    }

    public DIH4JDABuilder setCommandType(SlashCommandType type) {
        commandType = type;
        return this;
    }

    public DIH4JDA build() {
        if (jda == null) throw new IllegalStateException("JDA instance may not be empty.");
        if (commandType == null) throw new IllegalStateException("Command Type may not be empty.");
        if (ClasspathHelper.forPackage(commandsPackage).isEmpty())
            throw new IllegalArgumentException("Package " + commandsPackage + " does not exist.");

        return new DIH4JDA(jda, SlashCommandType.GUILD, commandsPackage, ownerId);
    }
}
