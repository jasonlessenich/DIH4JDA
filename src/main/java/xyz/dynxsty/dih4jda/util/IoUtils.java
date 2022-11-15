package xyz.dynxsty.dih4jda.util;

import javax.annotation.Nonnull;

/**
 * Utility class that contains some useful methods regarding IO.
 *
 * @since v1.6
 */
public class IoUtils {
    private IoUtils() {}

    /**
     * Tries to get the classloader for the given class.
     * @param clazz The class you want to get the classloader from.
     * @return The {@link ClassLoader} for the given class or the context-classloader if
     * the {@link ClassLoader} was null.
     */
    public static @Nonnull ClassLoader getClassLoaderForClass(@Nonnull Class<?> clazz) {
        return clazz.getClassLoader() == null ? Thread.currentThread().getContextClassLoader() : clazz.getClassLoader();
    }
}
