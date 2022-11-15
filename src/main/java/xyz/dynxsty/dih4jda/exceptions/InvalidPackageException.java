package xyz.dynxsty.dih4jda.exceptions;

import javax.annotation.Nonnull;

/**
 * Exception that is thrown for an invalid command package configuration.
 */
public class InvalidPackageException extends DIH4JDAException {
	/**
	 * Creates a new {@link InvalidPackageException}.
	 *
	 * @param message The exceptions' message.
	 */
	public InvalidPackageException(@Nonnull String message) {
		super(message);
	}
}
