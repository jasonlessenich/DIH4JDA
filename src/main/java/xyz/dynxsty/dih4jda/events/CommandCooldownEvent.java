package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.interactions.commands.RestrictedCommand;

import java.time.Duration;

/**
 * An event that gets fired when the user, which invoked the command, is not yet able to use this command due to
 * a specified {@link RestrictedCommand#setCommandCooldown(Duration) Command Cooldown}
 *
 * <h2>Command Cooldowns DO NOT persist between sessions!</h2>
 *
 * @see RestrictedCommand#setCommandCooldown(Duration)
 */
public class CommandCooldownEvent extends DIH4JDAEvent<CommandInteraction> {

	private final RestrictedCommand.Cooldown cooldown;

	public CommandCooldownEvent(DIH4JDA dih4jda, CommandInteraction interaction, RestrictedCommand.Cooldown cooldown) {
		super("onCommandCooldown", dih4jda, interaction);
		this.cooldown = cooldown;
	}

	public RestrictedCommand.Cooldown getCooldown() {
		return cooldown;
	}
}
