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
     * Checks if the given search object is inside the given array.
     *
     * @param array the array to check.
     * @param search the {@link Object} to search for.
     * @return true if the object is inside the array, otherwise false.
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
