package xyz.dynxsty.dih4jda.interactions.commands.application;

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import xyz.dynxsty.dih4jda.interactions.commands.ExecutableCommand;
import xyz.dynxsty.dih4jda.interactions.commands.RestrictedCommand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Model class for all <a href="https://discord.com/developers/docs/interactions/application-commands">Application Commands</a>.
 * For top-level commands, see {@link BaseApplicationCommand} which features an additional {@link RegistrationType}.
 *
 * @param <E> The event this class uses.
 * @param <T> The type of {@link net.dv8tion.jda.api.interactions.commands.build.CommandData} this class uses.
 */
public abstract class ApplicationCommand<E extends GenericCommandInteractionEvent, T> extends RestrictedCommand implements ExecutableCommand<E> {
	private T data;

	/**
	 * Creates a default {@link ApplicationCommand}.
	 */
	public ApplicationCommand() {}

	/**
	 * Sets this commands' {@link E CommandData}.
	 *
	 * @param data The {@link E CommandData} which should be used for this application command.
	 */
	public final void setCommandData(@Nonnull T data) {
		this.data = data;
	}

	/**
	 * Gets the command data that was previously set with {@link ApplicationCommand#setCommandData(Object)}
	 *
	 * @return the command data object.
	 */
	@Nullable
	public final T getCommandData() {
		return data;
	}
}
