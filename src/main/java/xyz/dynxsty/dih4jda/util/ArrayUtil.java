package xyz.dynxsty.dih4jda.util;

import javax.annotation.Nonnull;

/**
 * Utility class that contains some useful methods regarding arrays.
 *
 * @since v1.6
 */
public class ArrayUtil {

    private ArrayUtil() {}

    /**
     * Checks if the specified object is inside the specifed array.
     *
     * @param array The array to check.
     * @param search The {@link Object} to search for.
     * @return Whether the object is contained inside the array.
     */
    public static boolean contains(@Nonnull Object[] array, @Nonnull Object search) {
        for (Object obj : array) {
            if (obj.equals(search)) {
                return true;
            }
        }
        return false;
    }
}
