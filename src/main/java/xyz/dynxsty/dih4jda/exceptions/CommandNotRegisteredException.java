package xyz.dynxsty.dih4jda.exceptions;

public class CommandNotRegisteredException extends DIH4JDAException {
	public CommandNotRegisteredException(String message) {
		super(message);
	}

	public CommandNotRegisteredException(String message, Throwable t) {
		super(message, t);
	}
}
