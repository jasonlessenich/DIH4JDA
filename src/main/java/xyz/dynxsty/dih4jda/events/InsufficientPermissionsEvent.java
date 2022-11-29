package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.interactions.commands.RestrictedCommand;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * An event that gets fired when the user, which invoked the command, does NOT have one of the required permissions.
 *
 * @see RestrictedCommand#setRequiredPermissions(Permission...)
 */
public class InsufficientPermissionsEvent extends DIH4JDAInteractionEvent<CommandInteraction> {

	private final Set<Permission> permissions;

	/**
	 * Creates an instance of the {@link InsufficientPermissionsEvent}.
	 *
	 * @param dih4jda the {@link DIH4JDA} that fired this event.
	 * @param interaction the {@link CommandInteraction}.
	 * @param permissions the {@link Permission} that was required.
	 */
	public InsufficientPermissionsEvent(@Nonnull DIH4JDA dih4jda, @Nonnull CommandInteraction interaction,
										@Nonnull Set<Permission> permissions) {
		super("onInsufficientPermissions", dih4jda, interaction);
		this.permissions = permissions;
	}

	/**
	 * Gets you all the permissions that are required to perform the {@link RestrictedCommand} that causes this event.
	 *
	 * @return An immutable {@link Set} of all "required" permissions for the executed command.
	 * @see RestrictedCommand#setRequiredPermissions(Permission...)
	 */
	@Nonnull
	public Set<Permission> getPermissions() {
		return permissions;
	}
}
