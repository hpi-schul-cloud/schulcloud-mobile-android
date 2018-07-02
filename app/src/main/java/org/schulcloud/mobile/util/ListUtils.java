package org.schulcloud.mobile.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Date: 2/19/2018
 */
public final class ListUtils {
    public static <T> boolean contains(@NonNull T[] array, @Nullable T element) {
        for (T item : array)
            if (equals(item, element))
                return true;
        return false;
    }
    public static boolean equals(@Nullable Object a, @Nullable Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}
