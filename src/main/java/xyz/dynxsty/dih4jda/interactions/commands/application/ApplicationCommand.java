package xyz.dynxsty.dih4jda.interactions.commands.application;

import xyz.dynxsty.dih4jda.interactions.commands.ExecutableCommand;
import xyz.dynxsty.dih4jda.interactions.commands.RestrictedCommand;

public abstract class ApplicationCommand<E, T> extends RestrictedCommand implements ExecutableCommand<E> {
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
}
