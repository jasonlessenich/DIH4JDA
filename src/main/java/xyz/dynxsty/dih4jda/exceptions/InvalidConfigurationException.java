package xyz.dynxsty.dih4jda.exceptions;

import javax.annotation.Nonnull;

/**
 * Exception that is thrown for an invalid configuration.
 */
public class InvalidConfigurationException extends DIH4JDAException {
	/**
	 * Creates a new {@link InvalidConfigurationException}.
	 *
	 * @param message The exceptions' message.
	 */
	public InvalidConfigurationException(@Nonnull String message) {
		super(message);
	}
}
