package com.dynxsty.dih4jda.exceptions;

public class InvalidPackageException extends DIH4JDAException {
	public InvalidPackageException(String message) {
		super(message);
	}

	public InvalidPackageException(String message, Throwable t) {
		super(message, t);
	}
}
