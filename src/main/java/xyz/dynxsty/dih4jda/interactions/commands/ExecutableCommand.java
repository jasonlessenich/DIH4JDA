package xyz.dynxsty.dih4jda.interactions.commands;

import javax.annotation.Nonnull;

/**
 * Represents an executable command.
 * @param <E> The corresponding event.
 */
public interface ExecutableCommand<E> {

	/**
	 * The method that gets called once the command gets executed.
	 * @param event the command that is getting passed.
	 */
	void execute(@Nonnull E event);
}
