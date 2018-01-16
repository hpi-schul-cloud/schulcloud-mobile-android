package org.schulcloud.mobile.util;

import android.support.annotation.NonNull;

/**
 * Date: 12/7/2017
 */
public interface Action<T> {

    void call(@NonNull T t);

}
