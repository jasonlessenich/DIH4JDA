package xyz.dynxsty.dih4jda.interactions.commands.application;

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.interactions.commands.application.internal.RegistrationType;

/**
 * An extension of {@link ApplicationCommand} which contains a {@link RegistrationType}.
 * This abstract class is meant for top-level commands, which can be queued either per-guild or globally.
 *
 * @param <E> The event this class uses.
 * @param <T> The type of {@link net.dv8tion.jda.api.interactions.commands.build.CommandData} this class uses.
 */
public abstract class BaseApplicationCommand<E extends GenericCommandInteractionEvent, T> extends ApplicationCommand<E, T> {

	private RegistrationType registrationType = DIH4JDA.getDefaultRegistrationType();

	/**
	 * The {@link RegistrationType} the command got assigned.
	 *
	 * @return the {@link RegistrationType}.
	 */
	public final RegistrationType getRegistrationType() {
		return registrationType;
	}

	/**
	 * How the command should be queued. This DOES NOT work with {@link SlashCommand.Subcommand}!
	 *
	 * @param type the {@link RegistrationType} to set.
	 */
	public final void setRegistrationType(RegistrationType type) {
		this.registrationType = type;
	}
}
