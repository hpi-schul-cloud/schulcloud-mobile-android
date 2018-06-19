package org.schulcloud.mobile.utils

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.os.Bundle

/**
 * Date: 6/15/2018
 */

fun Map<String, String>.asBundle(): Bundle {
    return Bundle().apply {
        for (entry in entries)
            putString(entry.key, entry.value)
    }
}

fun <T, R> LiveData<T>.map(func: (T) -> R): LiveData<R> = Transformations.map(this, func)
fun <T, R> LiveData<T>.switchMap(func: (T) -> LiveData<R>): LiveData<R> = Transformations.switchMap(this, func)
