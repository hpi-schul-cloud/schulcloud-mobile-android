package org.schulcloud.mobile.utils

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
