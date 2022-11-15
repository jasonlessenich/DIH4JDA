package xyz.dynxsty.dih4jda.exceptions;

import javax.annotation.Nonnull;

/**
 * An exception that is thrown if a command is not registered by the {@link xyz.dynxsty.dih4jda.DIH4JDA} instance.
 */
public class CommandNotRegisteredException extends DIH4JDAException {
	/**
	 * Creates a new instance of this exception.
	 *
	 * @param message the message as a {@link String} you want to provide for the user.
	 */
	public CommandNotRegisteredException(@Nonnull String message) {
		super(message);
	}
}
