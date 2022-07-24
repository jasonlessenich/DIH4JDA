package com.dynxsty.dih4jda.util;

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
		return ClassUtils.doesImplement(base, implementation);
	}
}
