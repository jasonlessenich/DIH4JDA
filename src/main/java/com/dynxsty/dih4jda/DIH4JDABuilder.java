package com.dynxsty.dih4jda;

import com.dynxsty.dih4jda.exceptions.DIH4JDAException;
import com.dynxsty.dih4jda.exceptions.InvalidPackageException;
import net.dv8tion.jda.api.JDA;
import org.reflections.util.ClasspathHelper;

import javax.annotation.Nonnull;

/**
 * Builder-System used to build {@link DIH4JDA}.
 */
public class DIH4JDABuilder {
    private long ownerId;
    private String commandsPackage;
    private JDA jda;

    private DIH4JDABuilder(@Nonnull JDA jda) {
        this.jda = jda;
    }

    /**
     * Sets the {@link JDA} instance the handler will be used for.
     * @param instance The {@link JDA} instance.
     */
    public static DIH4JDABuilder setJDA(JDA instance) {
        return new DIH4JDABuilder(instance);
    }

    /**
     * Sets the owner of the Bot. This is used for admin-only commands which can only be executed by the specified owner.
     *
     * If this is not set admin-only commands will not work.
     * @param id The ID of the owner.
     */
    @Nonnull
    public DIH4JDABuilder setOwnerId(@Nonnull long id) {
        this.ownerId = id;
        return this;
    }

    /**
     * Sets the package that houses all Command classes. DIH4JDA then uses Reflection to "scan" the package for these classes.
     * @param pack The package's name.
     */
    @Nonnull
    public DIH4JDABuilder setCommandsPackage(@Nonnull String pack) {
        this.commandsPackage = pack;
        return this;
    }

    /**
     * Returns a {@link DIH4JDA} instance that has been validated.
     * @return the built, usable {@link DIH4JDA}
     */
    public DIH4JDA build() throws DIH4JDAException {
        if (jda == null) throw new IllegalStateException("JDA instance may not be empty.");
        if (ClasspathHelper.forPackage(commandsPackage).isEmpty()) {
            throw new InvalidPackageException("Package " + commandsPackage + " does not exist.");
        }
        return new DIH4JDA(jda, commandsPackage, ownerId);
    }
}
