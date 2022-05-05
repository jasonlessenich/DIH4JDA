package com.dynxsty.dih4jda.util;

import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for working with classes.
 *
 * @since v1.4
 */
public class ClassUtils {

	private ClassUtils() {
	}

	/**
	 * Checks if the given base class implements a certain class.
	 *
	 * @param base           The base class.
	 * @param implementation The implementation that should be checked.
	 * @return Whether the base class is implementing the given class.
	 * @since v1.4
	 */
	public static boolean doesImplement(Class<?> base, Class<?> implementation) {
		return implementation.isAssignableFrom(base);
	}

	/**
	 * Creates a new Instance of the given class.
	 *
	 * @param guild The slash command's guild. (if available)
	 * @param clazz The slash command's class.
	 * @return The Instance as a generic Object.
	 * @throws ReflectiveOperationException If an error occurs.
	 */
	public static Object getInstance(@Nullable Guild guild, Class<?> clazz) throws ReflectiveOperationException {
		for (Constructor<?> constructor : clazz.getConstructors()) {
			List<Class<?>> params = Arrays.asList(constructor.getParameterTypes());
			if (params.contains(Guild.class)) {
				if (guild != null) {
					return clazz.getConstructor(Guild.class).newInstance(guild);
				}
			} else if (params.isEmpty()) {
				return clazz.getConstructor().newInstance();
			} else {
				throw new IllegalArgumentException("Cannot access constructor with types: " + params);
			}
		}
		return null;
	}
}
