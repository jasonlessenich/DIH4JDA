package xyz.dynxsty.dih4jda.exceptions;

/**
 * Exception that is thrown for an invalid command package configuration.
 */
public class InvalidPackageException extends DIH4JDAException {
	/**
	 * Creates a new {@link InvalidPackageException}.
	 *
	 * @param message the message as a {@link String} you want to provide for the user-
	 */
	public InvalidPackageException(String message) {
		super(message);
	}
}
