package xyz.dynxsty.dih4jda.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for working with classes.
 *
 * @since v1.4
 */
public class ClassUtils {

	private ClassUtils() {}

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
	 * @param clazz The slash command's class.
	 * @return The Instance as a generic Object.
	 * @throws ReflectiveOperationException If an error occurs.
	 * @since v1.5.3
	 */
	public static Object getInstance(Class<?> clazz) throws ReflectiveOperationException {
		if (Modifier.isAbstract(clazz.getModifiers())) return null;
		for (Constructor<?> constructor : clazz.getConstructors()) {
			List<Class<?>> params = Arrays.asList(constructor.getParameterTypes());
			if (params.isEmpty()) {
				return clazz.getConstructor().newInstance();
			} else {
				throw new IllegalArgumentException("Cannot access constructor with types: " + params);
			}
		}
		return null;
	}
}
