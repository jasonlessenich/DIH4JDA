package xyz.dynxsty.dih4jda.exceptions;

/**
 * Exception that is thrown for everything that is related to DIH4JDA.
 */
public class DIH4JDAException extends Exception {
	/**
	 * Creates a new instance of this exception.
	 *
	 * @param message the message as a {@link String} you want to provide for the user.
	 */
	public DIH4JDAException(String message) {
		super(message);
	}

	/**
	 * Creates a new instance of this exception if it was caused by another {@link Throwable}.
	 *
	 * @param cause the {@link Throwable} that caused the {@link DIH4JDAException}.
	 */
	public DIH4JDAException(Throwable cause) {
		super(cause);
	}
}
