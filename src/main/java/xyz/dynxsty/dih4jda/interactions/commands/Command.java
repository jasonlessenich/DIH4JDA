package xyz.dynxsty.dih4jda.interactions.commands;

import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.DIH4JDABuilder;
import xyz.dynxsty.dih4jda.util.Pair;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;

// TODO: Docs
public abstract class Command {
	private RegistrationType type = DIH4JDA.defaultCommandType;

	private Pair<Boolean, Long[]> requiredGuilds = new Pair<>(null, null);
	private Permission[] requiredPermissions = new Permission[]{};
	private Long[] requiredUsers = new Long[]{};
	private Long[] requiredRoles = new Long[]{};

	public final RegistrationType getRegistrationType() {
		return type;
	}

	/**
	 * @param type How the command should be queued. This DOES NOT work with {@link SlashCommand.Subcommand}!
	 */
	public final void setRegistrationType(RegistrationType type) {
		this.type = type;
	}

	// TODO: Better Docs
	// TODO: Check Subcommand

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

	// TODO: Better Docs

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

	public final Long[] getRequiredUsers() {
		return requiredUsers;
	}

	/**
	 * Allows to require a set of {@link Long}s (user Ids) which are able to execute the corresponding command.
	 *
	 * @param users The set of {@link Long}s (user Ids).
	 */
	public final void setRequiredUsers(@Nonnull Long... users) {
		requiredUsers = users;
	}

	public final Long[] getRequiredRoles() {
		return requiredRoles;
	}

	/**
	 * Allows to require a set of {@link Long}s (role Ids) which are able to execute the corresponding command.
	 *
	 * @param roles The set of {@link Long}s (role Ids).
	 */
	public final void setRequiredRoles(@Nonnull Long... roles) {
		requiredRoles = roles;
	}
}
