package xyz.dynxsty.dih4jda.interactions.commands;

import javax.annotation.Nonnull;

/**
 * Represents a command that can be executed.
 * @param <E> the event to pass to the command.
 */
public interface ExecutableCommand<E> {

	/**
	 * The method that gets called once the command gets executed.
	 * @param event the command that is getting passed.
	 */
	void execute(E event);

	/**
	 * Gets the linked {@link SlashCommand}.
	 * @return the {@link SlashCommand} instance.
	 */
	@Nonnull
	SlashCommand getSlashCommand();
}
