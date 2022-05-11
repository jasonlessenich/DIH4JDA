package com.dynxsty.dih4jda.interactions.commands;

import com.dynxsty.dih4jda.DIH4JDA;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ExecutableCommand extends CommandRequirements {
    private final Set<Long> whitelistedGuilds = new HashSet<>();
    private final Set<Long> blacklistedGuilds = new HashSet<>();
    private Type type = DIH4JDA.defaultCommandType;

    /**
     * Allows a set of {@link Guild}s to update their Slash Commands.
     *
     * @param whitelisted An array of {@link Long}s.
     */
    public void whitelistGuilds(Long... whitelisted) {
        if (type != Type.GUILD)
            throw new UnsupportedOperationException("Cannot whitelist Guilds for Global Commands!");
        whitelistedGuilds.addAll(Arrays.asList(whitelisted));
    }

    /**
     * Prevents the given set of {@link Guild}s from updating their Slash Commands.
     *
     * @param blacklisted An array of {@link Long}s.
     */
    public void blacklistGuilds(Long... blacklisted) {
        if (type != Type.GUILD)
            throw new UnsupportedOperationException("Cannot blacklist Guilds for Global Commands!");
        blacklistedGuilds.addAll(Arrays.asList(blacklisted));
    }

    /**
     * Allows a set of {@link Guild}s to update their Slash Commands.
     *
     * @param whitelisted An array of {@link Guild}s.
     */
    public void whitelistGuilds(Guild... whitelisted) {
        if (type != Type.GUILD)
            throw new UnsupportedOperationException("Cannot whitelist Guilds for Global Commands!");
        whitelistedGuilds.addAll(Arrays.stream(whitelisted).map(Guild::getIdLong).collect(Collectors.toSet()));
    }

    /**
     * Prevents the given set of {@link Guild}s from updating their Slash Commands.
     *
     * @param blacklisted An array of {@link Guild}s.
     */
    public void blacklistGuilds(Guild... blacklisted) {
        if (type != Type.GUILD)
            throw new UnsupportedOperationException("Cannot blacklist Guilds for Global Commands!");
        blacklistedGuilds.addAll(Arrays.stream(blacklisted).map(Guild::getIdLong).collect(Collectors.toSet()));
    }

    /**
     * Gets all Guilds whose Slash Commands should be updated.
     *
     * @param jda The {@link JDA} instance.
     * @return A {@link List} with all Guilds.
     */
    public Set<Guild> getGuilds(JDA jda) {
        Set<Guild> guilds = new HashSet<>(jda.getGuilds());
        guilds.removeIf(g -> blacklistedGuilds.contains(g.getIdLong()));
        if (!whitelistedGuilds.isEmpty()) {
            guilds = whitelistedGuilds.stream().map(jda::getGuildById).collect(Collectors.toSet());
        }
        return guilds;
    }

    public Type getType() {
        return type;
    }

    /**
     * @param type Whether this command should be queued as a guild command.
     */
    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        GLOBAL,
        GUILD
    }
}
