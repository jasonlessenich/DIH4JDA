package xyz.dynxsty.dih4jda.util;

import net.dv8tion.jda.api.utils.data.DataObject;

import javax.annotation.Nonnull;
import java.util.Arrays;

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
     * @since v1.6
     */
    public static boolean contains(@Nonnull Object[] array, @Nonnull Object search) {
        for (Object obj : array) {
            if (obj.equals(search)) {
                return true;
            }
        }
        return false;
    }

    public static byte[] getSortedArrayFromDataObject(@Nonnull DataObject dataObject) {
        byte[] array = dataObject.toJson();
        Arrays.sort(array);
        return array;
    }
}
