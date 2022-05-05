package com.dynxsty.dih4jda.util;

import com.dynxsty.dih4jda.DIH4JDALogger;
import com.dynxsty.dih4jda.interactions.commands.GuildInteraction;

/**
 * Utility class for checking certain conditions.
 *
 * @since v1.4
 */
public class Checks {

	private Checks() {
	}

	/**
	 * Checks if the given base class implements a certain class.
	 *
	 * @param base           The base class.
	 * @param implementation The implementation that should be checked.
	 * @return Whether the base class is implementing the given class.
	 * @since v1.4
	 */
	public static boolean checkImplementation(Class<?> base, Class<?> implementation) {
		boolean doesImplement = ClassUtils.doesImplement(base, implementation);
		if (!doesImplement) {
			DIH4JDALogger.warn(String.format("Class %s does not implement %s. It will be ignored.",
					base.getSimpleName(), implementation.getSimpleName()));
		}
		return doesImplement;
	}
}
