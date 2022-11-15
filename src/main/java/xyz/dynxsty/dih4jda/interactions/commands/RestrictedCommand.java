package xyz.dynxsty.dih4jda.interactions.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import xyz.dynxsty.dih4jda.interactions.commands.application.CooldownType;
import xyz.dynxsty.dih4jda.util.Pair;

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
	private final Map<Pair<Long, Long>, Cooldown> COOLDOWN_CACHE = new HashMap<>();

	private Long[] requiredGuilds = new Long[]{};
	private Permission[] requiredPermissions = new Permission[]{};
	private Long[] requiredUsers = new Long[]{};
	private Long[] requiredRoles = new Long[]{};
	private Duration commandCooldown = Duration.ZERO;
	private CooldownType cooldownType = CooldownType.USER_GLOBAL;

	/**
	 * Creates a default instance.
	 */
	public RestrictedCommand() {}

	/**
	 * Allows to require a set of {@link Guild}s only in which the command can get executed.
	 *
	 * @param guilds        A {@link Long} array, containting the guild ids.
	 */
	public final void setRequiredGuilds(@Nonnull Long... guilds) {
		requiredGuilds = guilds;
	}

	/**
	 * The required guilds the command can be executed in.
	 *
	 * @return The {@link Long} array containing the guild ids.
	 */
	@Nonnull
	public Long[] getRequiredGuilds() {
		return requiredGuilds;
	}

	/**
	 * The required {@link Permission} the {@link net.dv8tion.jda.api.entities.User} needs to execute the command.
	 *
	 * @return the {@link Permission}.
	 */
	@Nonnull
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
	@Nonnull
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
	@Nonnull
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
	public void setCommandCooldown(@Nonnull Duration commandCooldown, @Nonnull CooldownType type) {
		this.commandCooldown = commandCooldown;
		this.cooldownType = type;
	}

	/**
	 * Returns the {@link Duration} the user has to wait between command executions.
	 *
	 * @return The {@link Duration}.
	 * @see RestrictedCommand#setCommandCooldown(Duration, CooldownType)
	 */
	@Nonnull
	public Pair<Duration, CooldownType> getCommandCooldown() {
		return new Pair<>(commandCooldown, cooldownType);
	}

	/**
	 * Manually applies a cooldown for the specified user id.<br>
	 * <b>Command Cooldowns DO NOT persist between sessions!</b><br>
	 *
	 * @param userId The id of the user you want to apply the cooldown on.
	 */
	public void applyCooldown(long userId, long guildId, @Nonnull Instant nextUse, @Nonnull CooldownType type) {
		COOLDOWN_CACHE.put(new Pair<>(userId, guildId), new Cooldown(Instant.now(), nextUse, type));
	}

	//Type: User / Global
	public void applyCooldown(@Nonnull User user, @Nonnull Instant nextUse) {
		applyCooldown(user.getIdLong(), 0, nextUse, CooldownType.USER_GLOBAL);
	}

	//Type: User / Guild
	public void applyCooldown(@Nonnull User user, @Nonnull Guild guild, @Nonnull Instant nextUse) {
		applyCooldown(user.getIdLong(), guild.getIdLong(), nextUse, CooldownType.USER_GUILD);
	}

	//Type: everyone / Guild
	public void applyCooldown(@Nonnull Guild guild, @Nonnull Instant nextUse) {
		applyCooldown(0, guild.getIdLong(), nextUse, CooldownType.GUILD);
	}

	/**
	 * Gets the {@link Cooldown time} the specified user can execute this command again.
	 * If the user has not executed the command yet, this will return a {@link Cooldown} with
	 * both the nextUse and the lastUse of {@link Instant#EPOCH} instead.
	 *
	 * @param userId The targets' user id.
	 * @return The {@link Instant} that marks the time the command can be used again.
	 */
	@Nonnull
	public Cooldown retrieveCooldown(long userId, long guildId) {
		Cooldown cooldown = COOLDOWN_CACHE.get(new Pair<>(userId, guildId));
		if (cooldown == null) {
			return new Cooldown(Instant.EPOCH, Instant.EPOCH, CooldownType.USER_GLOBAL);
		}
		return cooldown;
	}

	/**
	 * Returns whether the command can be used by the specified user.<br>
	 *
	 * <b>Command Cooldowns DO NOT persist between sessions!</b><br>
	 *
	 * @param userId The targets' user id.
	 * @param guildId The targets' guild id.
	 * @return Whether the command can be executed.
	 */
	public boolean hasCooldown(long userId, long guildId) {
		Cooldown cooldown;
		if (COOLDOWN_CACHE.get(new Pair<>(userId, 0L)) != null) {
			cooldown = COOLDOWN_CACHE.get(new Pair<>(userId, 0L));
		} else if (COOLDOWN_CACHE.get(new Pair<>(0L, guildId)) != null) {
			cooldown = COOLDOWN_CACHE.get(new Pair<>(0L, guildId));
		} else {
			cooldown = retrieveCooldown(userId, guildId);
		}
		return cooldown.getNextUse().isAfter(Instant.now());
	}

	/**
	 * Model class which represents a single command cooldown.
	 * <p>
	 * <b>Command Cooldowns DO NOT persist between sessions!</b>
	 */
	public static class Cooldown {

		private final Instant lastUse;
		private final Instant nextUse;
		private final CooldownType type;

		protected Cooldown(@Nonnull Instant lastUse, @Nonnull Instant nextUse, @Nonnull CooldownType type) {
			this.lastUse = lastUse;
			this.nextUse = nextUse;
			this.type = type;
		}

		/**
		 * Gets you the {@link Instant} of when a user can use the {@link RestrictedCommand} the next time.
		 *
		 * @return The next {@link Instant time} the command may be used again.
		 */
		@Nonnull
		public Instant getNextUse() {
			return nextUse;
		}

		/**
		 * Gets you the {@link Instant} the user has used the {@link RestrictedCommand} the last time.
		 *
		 * @return The last {@link Instant time} the command was used.
		 */
		@Nonnull
		public Instant getLastUse() {
			return lastUse;
		}

		@Nonnull
		public CooldownType getType() {
			return type;
		}
	}
}
