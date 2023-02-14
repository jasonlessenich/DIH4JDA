package xyz.dynxsty.dih4jda.interactions.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import xyz.dynxsty.dih4jda.interactions.commands.application.CooldownType;
import xyz.dynxsty.dih4jda.util.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

	private final Map<Long, Cooldown> COOLDOWN_USER_GLOBAL = new HashMap<>();
	private final Map<Pair<Long, Long>, Cooldown> COOLDOWN_USER_GUILD = new HashMap<>();
	private final Map<Long, Cooldown> COOLDOWN_GUILD = new HashMap<>();

	private Long[] requiredGuilds = new Long[]{};
	private Permission[] requiredPermissions = new Permission[]{};
	private Long[] requiredUsers = new Long[]{};
	private Long[] requiredRoles = new Long[]{};
	private Duration commandCooldown = Duration.ZERO;
	private CooldownType cooldownType = CooldownType.NONE;

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
	 * @param type The {@link CooldownType} you want to use.
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
	 * Manually applies a cooldown with the type {@link CooldownType#USER_GLOBAL} to the provided {@link User}.
	 *
	 * @param user The {@link User} who the {@link Cooldown} should apply to.
	 * @param  nextUse The time as an {@link Instant} where the user can execute the command the next time.
	 */
	public void applyCooldown(@Nonnull User user, @Nonnull Instant nextUse) {
		COOLDOWN_USER_GLOBAL.put(user.getIdLong(), Cooldown.forNextUse(nextUse));
	}

	/**
	 * Manually applies a cooldown with the type {@link CooldownType#GUILD} to the provided {@link Guild}.
	 *
	 * @param guild The {@link Guild} where the {@link Cooldown} should apply to.
	 * @param nextUse The time as an {@link Instant} where the user can execute the command the next time.
	 */
	public void applyCooldown(@Nonnull Guild guild, @Nonnull Instant nextUse) {
		COOLDOWN_GUILD.put(guild.getIdLong(), Cooldown.forNextUse(nextUse));
	}

	/**
	 * Manually applies a cooldown with the type {@link CooldownType#MEMBER_GUILD} to the provided {@link User} and {@link Guild}.
	 * @param user The {@link User} who the {@link Cooldown} should apply to.
	 * @param guild The {@link Guild} where the {@link Cooldown} should apply to.
	 * @param nextUse The time as an {@link Instant} where the user can execute the command the next time.
	 */
	public void applyCooldown(@Nonnull User user, @Nonnull Guild guild, @Nonnull Instant nextUse) {
		COOLDOWN_USER_GUILD.put(new Pair<>(user.getIdLong(), guild.getIdLong()), Cooldown.forNextUse(nextUse));
	}

	/**
	 * Checks if the provided {@link User} is on cooldown.<br>
	 * <b>Checks only for the {@link CooldownType#USER_GLOBAL} type.</b>
	 *
	 * @see RestrictedCommand#hasCooldown(Member)
	 * @param user The {@link User} to check.
	 * @return true if the user is on cooldown, false otherwise.
	 */
	public boolean hasCooldown(@Nonnull User user) {
		Cooldown cooldown = COOLDOWN_USER_GLOBAL.get(user.getIdLong());
		if (cooldown == null) return false;
		cleanUpCooldown(user,cooldown);
		return cooldown.isInCooldown();
	}

	/**
	 * Checks if the provided {@link User} is on cooldown.
	 *
	 * @see RestrictedCommand#hasCooldown(Member)
	 * @param user The {@link User} to check.
	 * @param guild The {@link Guild} to check.
	 * @return true if the user is on cooldown, false otherwise.
	 */
	private boolean hasCooldown(@Nonnull User user, @Nonnull Guild guild) {
		if (hasCooldown(user)) return true;
		Cooldown cooldown = COOLDOWN_GUILD.get(guild.getIdLong());
		if (cooldown != null) return cooldown.isInCooldown();
		cooldown = COOLDOWN_USER_GUILD.get(new Pair<>(user.getIdLong(), guild.getIdLong()));
		if (cooldown == null) return false;

		cleanUpCooldown(user, guild, cooldown);
		return cooldown.isInCooldown();
	}

	/**
	 * Checks if the provided {@link Member} is on cooldown.
	 *
	 * @param member The {@link Member} to check.
	 * @return true if the user is on cooldown, false otherwise.
	 */
	public boolean hasCooldown(@Nonnull Member member) {
		CooldownType type = retrieveCooldownType(member.getUser(), member.getGuild());
		switch (type) {
			case USER_GLOBAL:
				return hasCooldown(member.getUser());
			case GUILD:
			case MEMBER_GUILD:
				return hasCooldown(member.getUser(), member.getGuild());
			case NONE:
				return false;
		}
		return false;
	}

	/**
	 * Retrieves the {@link CooldownType} for the provided {@link User} and {@link Guild}.
	 * @param user The {@link User} to check.
	 * @param guild The {@link Guild} to check.
	 * @return The {@link CooldownType}.
	 */
	private CooldownType retrieveCooldownType(@Nonnull User user, @Nonnull Guild guild) {
		if (COOLDOWN_USER_GLOBAL.get(user.getIdLong()) != null) {
			return CooldownType.USER_GLOBAL;
		} else if (COOLDOWN_GUILD.get(guild.getIdLong()) != null) {
			return CooldownType.GUILD;
		} else if (COOLDOWN_USER_GUILD.get(Pair.of(user.getIdLong(), guild.getIdLong())) != null) {
			return CooldownType.MEMBER_GUILD;
		}
		return CooldownType.NONE;
	}

	/**
	 * Removes the {@link RestrictedCommand#COOLDOWN_GUILD} and {@link RestrictedCommand#COOLDOWN_USER_GUILD} map entries
	 * linked to the given inputs.
	 * @param user The {@link User} to clean up.
	 * @param guild The {@link Guild} to clean up.
	 */
	private void cleanUpCooldown(@Nonnull User user, @Nonnull Guild guild, @Nonnull Cooldown cooldown) {
		if (!cooldown.isInCooldown()) {
			COOLDOWN_USER_GUILD.remove(Pair.of(user.getIdLong(), guild.getIdLong()));
			COOLDOWN_GUILD.remove(guild.getIdLong());
		}
	}

	/**
	 * Removes the {@link RestrictedCommand#COOLDOWN_USER_GLOBAL} map entries linked to the given input.
	 * @param user The {@link User} to clean up.
	 */
	private void cleanUpCooldown(@Nonnull User user, @Nonnull Cooldown cooldown) {
		if (!cooldown.isInCooldown()) {
			COOLDOWN_USER_GLOBAL.remove(user.getIdLong());
		}
	}

	/**
	 * Retrieves the {@link Cooldown} for the provided {@link User} and {@link Guild}.
	 * @param user The {@link User} to get the cooldown for.
	 * @param guild The {@link Guild} to get the cooldown for
	 * @return The {@link Cooldown}.
	 */
	@Nonnull
	public Cooldown retrieveCooldown(@Nonnull User user, @Nullable Guild guild) {
		if (guild == null) return retrieveCooldown(user);
		CooldownType type = retrieveCooldownType(user, guild);
		switch (type) {
			case USER_GLOBAL:
				return COOLDOWN_USER_GLOBAL.get(user.getIdLong());
			case GUILD:
				return COOLDOWN_GUILD.get(guild.getIdLong());
			case MEMBER_GUILD:
				return COOLDOWN_USER_GUILD.get(Pair.of(user.getIdLong(), guild.getIdLong()));
			case NONE:
				return Cooldown.forNextUse(Instant.EPOCH);
		}
		return Cooldown.forNextUse(Instant.EPOCH);
	}

	/**
	 * Retrieves the {@link Cooldown} for the provided {@link User}.
	 * @param user The {@link User} to get the cooldown for.
	 * @return The {@link Cooldown}.
	 */
	@Nonnull
	public Cooldown retrieveCooldown(@Nonnull User user) {
		return COOLDOWN_USER_GLOBAL.get(user.getIdLong()) == null ? Cooldown.forNextUse(Instant.EPOCH) : COOLDOWN_USER_GLOBAL.get(user.getIdLong());
	}


	/**
	 * Model class which represents a single command cooldown.<br>
	 * <b>Command Cooldowns DO NOT persist between sessions!</b>
	 */
	public static class Cooldown {

		/**
		 * The {@link Instant} the user has used the {@link RestrictedCommand} the last time.
		 */
		private final Instant lastUse;
		/**
		 * The {@link Instant} of when a user can use the {@link RestrictedCommand} the next time.
		 */
		private final Instant nextUse;

		/**
		 * Creates a new {@link Cooldown} with the provided {@link Instant}s.
		 *
		 * @param lastUse The {@link Instant} the user has used the {@link RestrictedCommand} the last time.
		 * @param nextUse The {@link Instant} of when a user can use the {@link RestrictedCommand} the next time.
		 */
		private Cooldown(@Nonnull Instant lastUse, @Nonnull Instant nextUse) {
			this.lastUse = lastUse;
			this.nextUse = nextUse;
		}

		/**
		 * Creates a new {@link Cooldown} where the last use is set to the current {@link Instant} and
		 * the next use is set to the provided {@link Instant}.
		 *
		 * @param nextUse The {@link Instant} of when a user can use the {@link RestrictedCommand} the next time.
		 * @return The new {@link Cooldown}.
		 */
		@Nonnull
		public static Cooldown forNextUse(@Nonnull Instant nextUse) {
			return new Cooldown(Instant.now(), nextUse);
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

		/**
		 * Checks if the user is currently on cooldown.
		 *
		 * @return true if the user is on cooldown, false otherwise.
		 */
		public boolean isInCooldown() {
			return Instant.now().isBefore(nextUse);
		}

		/**
		 * Returns a string representation of the object.
		 *
		 * @return The representation as a {@link String}.
		 */
		@Override
		public String toString() {
			return "Cooldown{" +
					"lastUse=" + lastUse +
					", nextUse=" + nextUse +
					'}';
		}
	}
}
