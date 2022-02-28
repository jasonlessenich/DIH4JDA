package com.dynxsty.dih4jda.exceptions;

public class InvalidCommandException extends DIH4JDAException {
	public InvalidCommandException(String message) {
		super(message);
	}

	public InvalidCommandException(String message, Throwable t) {
		super(message, t);
	}
}
