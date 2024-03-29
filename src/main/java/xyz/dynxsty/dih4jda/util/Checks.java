package xyz.dynxsty.dih4jda.util;

import xyz.dynxsty.dih4jda.DIH4JDALogger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;

/**
 * Utility class for checking certain conditions.
 *
 * @since v1.4
 */
public class Checks {
	private Checks() {}

	/**
	 * Checks if the given base class implements a certain class.
	 *
	 * @param base           The base class.
	 * @param implementation The implementation that should be checked.
	 * @return Whether the base class is implementing the given class.
	 * @since v1.4
	 */
	public static boolean checkImplementation(@Nonnull Class<?> base, @Nonnull Class<?> implementation) {
		return ClassUtils.doesImplement(base, implementation);
	}

	/**
	 * Checks if the given class has an empty constructor.
	 *
	 * @param base The class to check.
	 * @return Whether the class has an empty constructor.
	 * @since v1.5.1
	 */
	public static boolean checkEmptyConstructor(@Nonnull Class<?> base) {
		for (Constructor<?> c : base.getConstructors()) {
			if (c.getParameterCount() == 0) return true;
		}
		DIH4JDALogger.warn(String.format("Class %s contains unknown constructor parameters!", base.getSimpleName()));
		return false;
	}

	/**
	 * Throws an {@link IllegalArgumentException} if the given {@link Object} is null.
	 *
	 * @param argument The {@link Object} to check.
	 * @param name The name of the {@link Object}.
	 * @throws IllegalArgumentException if the given argument is null.
	 */
	public static void notNull(@Nullable final Object argument, @Nonnull final String name) {
		if (argument == null) {
			throw new IllegalArgumentException(name + " may not be null");
		}
	}
}
