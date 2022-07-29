package com.dynxsty.dih4jda.exceptions;

/**
 * Exception that is thrown for everything that is related to DIH4JDA.
 */
public class DIH4JDAException extends Exception {

	public DIH4JDAException(String message) {
		super(message);
	}

	public DIH4JDAException(Throwable cause) {
		super(cause);
	}

	public DIH4JDAException(String message, Throwable t) {
		super(message, t);
	}
}
