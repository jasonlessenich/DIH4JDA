package xyz.dynxsty.dih4jda.interactions.commands;

import xyz.dynxsty.dih4jda.DIH4JDA;

public abstract class ApplicationCommand<E, T> extends RestrictedCommand implements ExecutableCommand<E> {

	private RegistrationType registrationType = DIH4JDA.defaultCommandType;
	private T data;

	/**
	 * Sets this commands' {@link E CommandData}.
	 *
	 * @param data The {@link E CommandData} which should be used for this application command.
	 */
	public final void setCommandData(T data) {
		this.data = data;
	}

	public final T getCommandData() {
		return data;
	}

	/**
	 * The {@link RegistrationType} the command got assigned.
	 *
	 * @return the {@link RegistrationType}.
	 */
	public RegistrationType getRegistrationType() {
		return registrationType;
	}

	/**
	 * How the command should be queued. This DOES NOT work with {@link SlashCommand.Subcommand}!
	 *
	 * @param type the {@link RegistrationType} to set.
	 */
	public void setRegistrationType(RegistrationType type) {
		this.registrationType = type;
	}
}
