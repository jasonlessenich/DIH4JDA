package xyz.dynxsty.dih4jda.exceptions;

import javax.annotation.Nonnull;

/**
 * The top-level {@link Exception} that is thrown for everything regarding DIH4JDA.
 */
public class DIH4JDAException extends Exception {
	/**
	 * Creates a new instance of this exception.
	 *
	 * @param message the message as a {@link String} you want to provide for the user.
	 */
	public DIH4JDAException(@Nonnull String message) {
		super(message);
	}

	/**
	 * Creates a new instance of this exception if it was caused by another {@link Throwable}.
	 *
	 * @param cause the {@link Throwable} that caused the {@link DIH4JDAException}.
	 */
	public DIH4JDAException(@Nonnull Throwable cause) {
		super(cause);
	}
}
