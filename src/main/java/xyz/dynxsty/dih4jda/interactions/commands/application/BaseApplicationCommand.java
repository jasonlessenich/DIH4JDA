package xyz.dynxsty.dih4jda.interactions.commands.application;

import xyz.dynxsty.dih4jda.DIH4JDA;

public abstract class BaseApplicationCommand<E, T> extends ApplicationCommand<E, T> {

	private RegistrationType registrationType = DIH4JDA.defaultRegistrationType;

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
