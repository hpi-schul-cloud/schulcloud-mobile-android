package org.schulcloud.mobile.controllers.base;

import android.support.annotation.NonNull;

/**
 * Shorthands don't seem to work when using Kotlin interface.
 * <p>
 * Date: 6/10/2018
 */
public interface OnItemSelectedCallback<T> {

    void onItemSelected(@NonNull T item);

}
