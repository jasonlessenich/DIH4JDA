package xyz.dynxsty.dih4jda.exceptions;

/**
 * Exception that is thrown for an invalid command package configuration.
 */
public class InvalidPackageException extends DIH4JDAException {
	public InvalidPackageException(String message) {
		super(message);
	}

	public InvalidPackageException(String message, Throwable t) {
		super(message, t);
	}
}
