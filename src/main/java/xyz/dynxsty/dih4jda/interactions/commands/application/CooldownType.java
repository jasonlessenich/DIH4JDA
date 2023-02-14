package xyz.dynxsty.dih4jda.interactions.commands.application;

/**
 * The supported {@link xyz.dynxsty.dih4jda.interactions.commands.RestrictedCommand.Cooldown} types.<br>
 * <b>Cooldowns does not work between different shards or DIH4JDA instances.</b>
 */
public enum CooldownType {
    /**
     * Limits the amount how often a user can execute a command.<br>
     * <b>User / Global</b>
     */
    USER_GLOBAL,
    /**
     * Limits the amount how often everyone can execute a command on a guild.<br>
     * <b>everyone / Guild</b>
     */
    GUILD,
    /**
     * Limits the amount how often a user can execute a command on a guild.<br>
     * <b>Member / Guild</b><br>
     */
    MEMBER_GUILD,
    /**
     * No cooldown is applied.
     */
    NONE
}
