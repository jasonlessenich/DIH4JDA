package com.dynxsty.dih4jda.exceptions;

/**
 * Exception that is thrown for an invalid slash command type configuration.
 */
public class InvalidTypeException extends DIH4JDAException {
	public InvalidTypeException(String message) {
		super(message);
	}

	public InvalidTypeException(String message, Throwable t) {
		super(message, t);
	}
}
