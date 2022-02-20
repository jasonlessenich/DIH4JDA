package com.dynxsty.dih4jda.exceptions;

public class InvalidTypeException extends DIH4JDAException {
	public InvalidTypeException(String message) {
		super(message);
	}

	public InvalidTypeException(String message, Throwable t) {
		super(message, t);
	}
}
