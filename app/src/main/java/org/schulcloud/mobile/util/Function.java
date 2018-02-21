package org.schulcloud.mobile.util;

import android.support.annotation.NonNull;

/**
 * Date: 2/20/2018
 */
public interface Function<T, R> {

    @NonNull
    R apply(@NonNull T t);

}
