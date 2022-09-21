package com.dynxsty.dih4jda.util;

import javax.annotation.Nonnull;

/**
 * Utility class that contains some useful methods regarding the IO.
 */
public class IOUtil {
    private IOUtil() {}

    /**
     * Tries to get the classloader for the given class.
     * @param clazz The class you want to get the classloader from.
     * @return The {@link ClassLoader} for the given class oder the context-classloader.
     */
    public static @Nonnull ClassLoader getClassLoaderForClass(@Nonnull Class<?> clazz) {
        return clazz.getClassLoader() == null ? Thread.currentThread().getContextClassLoader() : clazz.getClassLoader();
    }
}
