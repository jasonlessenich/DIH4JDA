package xyz.dynxsty.dih4jda.interactions.commands;

import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.DIH4JDABuilder;
import xyz.dynxsty.dih4jda.util.Pair;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Represents a basic command.
 *
 * @since v1.6
 */
public abstract class AbstractCommand {
	private RegistrationType type = DIH4JDA.defaultCommandType;

	//The command requirements
	private Pair<Boolean, Long[]> requiredGuilds = new Pair<>(null, null);
	private Permission[] requiredPermissions = new Permission[]{};
	private Long[] requiredUsers = new Long[]{};
	private Long[] requiredRoles = new Long[]{};

	/**
	 * The {@link RegistrationType} the command got assigned.
	 *
	 * @return the {@link RegistrationType}.
	 */
	public final RegistrationType getRegistrationType() {
		return type;
	}

	/**
	 * How the command should be queued. This DOES NOT work with {@link SlashCommand.Subcommand}!
	 *
	 * @param type the {@link RegistrationType} to set.
	 */
	public final void setRegistrationType(RegistrationType type) {
		this.type = type;
	}

	/**
	 * Allows a set of {@link Guild}s to update their Slash Commands.
	 *
	 * @param restrictQueue If enabled, this will only queue this command in the specified guilds. For that
	 *                      to work, the command MUST be of {@link RegistrationType#GUILD}. If
	 *                      {@link DIH4JDABuilder#setGuildSmartQueue(boolean)} is enabled, this
	 *                      will also delete the command in all the other guilds. This DOES NOT work with {@link SlashCommand.Subcommand}!
	 * @param guilds        An array of {@link Long}s.
	 */
	public final void setRequiredGuilds(boolean restrictQueue, Long... guilds) {
		if (restrictQueue && type != RegistrationType.GUILD) {
			throw new UnsupportedOperationException("Cannot restrict queue for Global Commands!");
		}
		requiredGuilds = new Pair<>(restrictQueue, guilds);
	}

	/**
	 * The guilds that the command should or should not be registered on.
	 * The {@link Boolean} represents either if the command should (true) or should not (false) be registered on
	 * these guilds.
	 * And the {@link Long} array contains the guild ids.
	 *
	 * @return a {@link Pair} containing the {@link Boolean} and {@link Long} array for the guild ids.
	 */
	public Pair<Boolean, Long[]> getRequiredGuilds() {
		return requiredGuilds;
	}

	/**
	 * Allows a set of {@link Guild}s to update their Slash Commands.
	 *
	 * @param guilds An array of {@link Long}s.
	 */
	public final void setRequiredGuilds(@Nonnull Long... guilds) {
		setRequiredGuilds(false, guilds);
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
	 * @return the {@link Long} array containing the user ids.
	 */
	public final Long[] getRequiredUsers() {
		return requiredUsers;
	}

	/**
	 * Allows to require a set of {@link Long}s (user Ids) which are able to execute the corresponding command.
	 *
	 * @param users the {@link net.dv8tion.jda.api.entities.User} ids as an {@link Long} array.
	 */
	public final void setRequiredUsers(@Nonnull Long... users) {
		requiredUsers = users;
	}

	/**
	 * The {@link net.dv8tion.jda.api.entities.Role}s that are required to execute the command.
	 *
	 * @return the {@link Long} array containing the role ids.
	 */
	public final Long[] getRequiredRoles() {
		return requiredRoles;
	}

	/**
	 * Allows to require a set of {@link Long}s (role Ids) which are able to execute the corresponding command.
	 *
	 * @param roles the {@link net.dv8tion.jda.api.entities.Role} ids as an {@link Long} array.
	 */
	public final void setRequiredRoles(@Nonnull Long... roles) {
		requiredRoles = roles;
	}
}
