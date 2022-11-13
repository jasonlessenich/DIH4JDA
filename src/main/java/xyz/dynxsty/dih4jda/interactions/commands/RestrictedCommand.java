package xyz.dynxsty.dih4jda.interactions.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a basic command.
 *
 * @since v1.6
 */
public abstract class RestrictedCommand {
	private final Map<Long, Cooldown> COOLDOWN_CACHE = new HashMap<>();

	private Long[] requiredGuilds = new Long[]{};
	private Permission[] requiredPermissions = new Permission[]{};
	private Long[] requiredUsers = new Long[]{};
	private Long[] requiredRoles = new Long[]{};
	private Duration commandCooldown = Duration.ZERO;

	/**
	 * Allows to require a set of {@link Guild}s only in which the command can get executed.
	 *
	 * @param guilds        A {@link Long} array, containting the guild ids.
	 */
	public final void setRequiredGuilds(Long... guilds) {
		requiredGuilds = guilds;
	}

	/**
	 * The required guilds the command can be executed in.
	 *
	 * @return The {@link Long} array containing the guild ids.
	 */
	public Long[] getRequiredGuilds() {
		return requiredGuilds;
	}

	/**
	 * The required {@link Permission} the {@link net.dv8tion.jda.api.entities.User} needs to execute the command.
	 *
	 * @return the {@link Permission}.
	 */
	public final Permission[] getRequiredPermissions() {
		return requiredPermissions;
	}

	/**
	 * Allows to require a set of {@link Permission}s which are needed to execute the corresponding command.
	 *
	 * @param permissions The set of {@link Permission}s.
	 */
	public final void setRequiredPermissions(@Nonnull Permission... permissions) {
		requiredPermissions = permissions;
	}

	/**
	 * The user ids from the {@link net.dv8tion.jda.api.entities.User} that are allowed to execute the command.
	 *
	 * @return The {@link Long} array containing the user ids.
	 */
	public final Long[] getRequiredUsers() {
		return requiredUsers;
	}

	/**
	 * Allows to require a set of {@link Long}s (user Ids) which are able to execute the corresponding command.
	 *
	 * @param users The {@link net.dv8tion.jda.api.entities.User} ids as an {@link Long} array.
	 */
	public final void setRequiredUsers(@Nonnull Long... users) {
		requiredUsers = users;
	}

	/**
	 * The {@link net.dv8tion.jda.api.entities.Role}s that are required to execute the command.
	 *
	 * @return The {@link Long} array containing the role ids.
	 */
	public final Long[] getRequiredRoles() {
		return requiredRoles;
	}

	/**
	 * Allows to require a set of {@link Long}s (role Ids) which are able to execute the corresponding command.
	 *
	 * @param roles The {@link net.dv8tion.jda.api.entities.Role} ids as an {@link Long} array.
	 */
	public final void setRequiredRoles(@Nonnull Long... roles) {
		requiredRoles = roles;
	}

	/**
	 * Allows to set a cooldown for this command.
	 * The user has to wait the provided {@link Duration} until they can execute this command again.
	 * If the user executes the command while they're on cooldown, the {@link xyz.dynxsty.dih4jda.events.CommandCooldownEvent}
	 * is fired.<br>
	 *
	 * <b>Command Cooldowns DO NOT persist between sessions!</b><br>
	 *
	 * @param commandCooldown The {@link Duration} the user has to wait between command executions.
	 */
	public void setCommandCooldown(Duration commandCooldown) {
		this.commandCooldown = commandCooldown;
	}

	/**
	 * Returns the {@link Duration} the user has to wait between command executions.
	 *
	 * @return The {@link Duration}.
	 * @see RestrictedCommand#setCommandCooldown(Duration)
	 */
	public Duration getCommandCooldown() {
		return commandCooldown;
	}

	/**
	 * Manually applies a cooldown for the specified user id.<br>
	 *
	 * <b>Command Cooldowns DO NOT persist between sessions!</b><br>
	 *
	 * @param userId The targets' user id.
	 * @param nextUse The {@link Instant} that marks the time the command can be used again.
	 */
	public void applyCooldown(long userId, Instant nextUse) {
		COOLDOWN_CACHE.put(userId, new Cooldown(Instant.now(), nextUse));
	}

	/**
	 * Gets the {@link Cooldown time} the specified user can execute this command again.
	 * If the user has not executed the command yet, this will return a {@link Cooldown} with
	 * both the nextUse and the lastUse of {@link Instant#EPOCH} instead.
	 *
	 * @param userId The targets' user id.
	 * @return The {@link Instant} that marks the time the command can be used again.
	 */
	public Cooldown retrieveCooldown(long userId) {
		Cooldown cooldown = COOLDOWN_CACHE.get(userId);
		if (cooldown == null) return new Cooldown(Instant.EPOCH, Instant.EPOCH);
		return cooldown;
	}

	/**
	 * Returns whether the command can be used by the specified user.<br>
	 *
	 * <b>Command Cooldowns DO NOT persist between sessions!</b><br>
	 *
	 * @param userId The targets' user id.
	 * @return Whether the command can be executed.
	 */
	public boolean hasCooldown(long userId) {
		return retrieveCooldown(userId).getNextUse().isAfter(Instant.now());
	}

	/**
	 * Model class which represents a single command cooldown.
	 *
	 * <h2>Command Cooldowns DO NOT persist between sessions!</h2>
	 */
	public static class Cooldown {
		private final Instant lastUse;
		private final Instant nextUse;

		protected Cooldown(Instant lastUse, Instant nextUse) {
			this.lastUse = lastUse;
			this.nextUse = nextUse;
		}

		/**
		 * @return The next {@link Instant time} the command may be used again.
		 */
		public Instant getNextUse() {
			return nextUse;
		}

		/**
		 * @return The last {@link Instant time} the command was used.
		 */
		public Instant getLastUse() {
			return lastUse;
		}
	}
}
