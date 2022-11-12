package xyz.dynxsty.dih4jda.interactions.commands.application.internal;

/**
 * Types of cooldowns that are supported.
 */
public enum CooldownType {
    /**
     * A global cooldown that limits the amount of executions of a command globally per user.
     * <br>
     * <b>User / Global</b>
     */
    GLOBAL,
    /**
     * A guild based cooldown that limits the amount of executions of a command guild based per user.
     * <br>
     * <b>User / Guild</b>
     */
    GUILD,
}
