package xyz.dynxsty.dih4jda.exceptions;

/**
 * Exception that is thrown for an invalid configuration.
 */
public class InvalidConfigurationException extends DIH4JDAException {
	/**
	 * Creates a new {@link InvalidConfigurationException}.
	 *
	 * @param message The exceptions' message.
	 */
	public InvalidConfigurationException(String message) {
		super(message);
	}
}
