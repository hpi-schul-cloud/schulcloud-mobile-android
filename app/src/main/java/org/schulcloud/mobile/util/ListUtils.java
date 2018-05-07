package org.schulcloud.mobile.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.internal.util.Predicate;

import java.util.Collection;

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
    @Nullable
    public static <T> T where(@NonNull Collection<T> collection, @NonNull Predicate<T> predicate) {
        for (T item : collection) {
            if (predicate.apply(item))
                return item;
        }
        return null;
    }
}
