package com.dynxsty.dih4jda;

import net.dv8tion.jda.api.JDA;
import org.reflections.util.ClasspathHelper;

/**
 * Builder-System used to build {@link DIH4JDA}.
 */
public class DIH4JDABuilder {

    private String ownerId, commandsPackage;
    private JDA jda;
    private SlashCommandType commandType;

    /**
     * Sets the {@link JDA} instance the handler will be used for.
     * @param instance The {@link JDA} instance.
     */
    public DIH4JDABuilder setJDA(JDA instance) {
        jda = instance;
        return this;
    }

    /**
     * Sets the owner of the Bot. This is used for admin-only commands which can only be executed by the specified owner.
     *
     * If this is not set admin-only commands will not work.
     * @param id The ID of the owner.
     */
    public DIH4JDABuilder setOwnerId(String id) {
        ownerId = id;
        return this;
    }

    /**
     * Sets the package that houses all Command classes. DIH4JDA then uses Reflection to "scan" the package for these classes.
     * @param pack The package's name.
     */
    public DIH4JDABuilder setCommandsPackage(String pack) {
        commandsPackage = pack;
        return this;
    }

    /**
     * Sets the {@link SlashCommandType} the handler should use.
     *
     * GUILD commands will still be present on all guilds. The Handler loops through all guilds the bot is in and adds them to all of them.
     * @param type The {@link SlashCommandType} that should be used.
     */
    public DIH4JDABuilder setCommandType(SlashCommandType type) {
        commandType = type;
        return this;
    }

    /**
     * Returns a {@link DIH4JDA} instance that has been validated.
     * @return the built, usable {@link DIH4JDA}
     */
    public DIH4JDA build() {
        if (jda == null) throw new IllegalStateException("JDA instance may not be empty.");
        if (commandType == null) throw new IllegalStateException("Command Type may not be empty.");
        if (ClasspathHelper.forPackage(commandsPackage).isEmpty())
            throw new IllegalArgumentException("Package " + commandsPackage + " does not exist.");

        return new DIH4JDA(jda, SlashCommandType.GUILD, commandsPackage, ownerId);
    }
}
