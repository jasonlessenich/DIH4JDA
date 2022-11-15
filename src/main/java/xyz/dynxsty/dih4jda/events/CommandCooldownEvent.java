package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.interactions.commands.RestrictedCommand;

import javax.annotation.Nonnull;
import java.time.Duration;

/**
 * An event that gets fired when the user, which invoked the command, is not yet able to use this command due to
 * a specified {@link RestrictedCommand#setCommandCooldown(Duration) Command Cooldown}
 *
 * <b>Command Cooldowns DO NOT persist between sessions!</b>
 *
 * @see RestrictedCommand#setCommandCooldown(Duration)
 */
public class CommandCooldownEvent extends DIH4JDAEvent<CommandInteraction> {

	private final RestrictedCommand.Cooldown cooldown;

	/**
	 * Creates a new instance of this event.
	 *
	 * @param dih4jda the {@link DIH4JDA} instance that fired this event.
	 * @param interaction the {@link CommandInteraction}.
	 * @param cooldown the {@link RestrictedCommand.Cooldown} the user has.
	 */
	public CommandCooldownEvent(@Nonnull DIH4JDA dih4jda, @Nonnull CommandInteraction interaction,
								@Nonnull RestrictedCommand.Cooldown cooldown) {
		super("onCommandCooldown", dih4jda, interaction);
		this.cooldown = cooldown;
	}

	/**
	 * Gets you the {@link RestrictedCommand.Cooldown} the user has.
	 *
	 * @return the {@link RestrictedCommand.Cooldown} instance.
	 */
	@Nonnull
	public RestrictedCommand.Cooldown getCooldown() {
		return cooldown;
	}
}
