@file:Suppress("TooManyFunctions")
package org.schulcloud.mobile.utils

import android.text.TextUtils
import java.io.File

fun combinePath(vararg parts: String): String {
    val builder = StringBuilder(parts[0])
    for (i in 1 until parts.size) {
        if (TextUtils.isEmpty(parts[i]))
            continue

        val endsWithSeparator = builder.isNotEmpty() && builder[builder.length - 1] == File.separatorChar
        val beginsWithSeparator = parts[i].isNotEmpty() && parts[i][0] == File.separatorChar

        if (endsWithSeparator && beginsWithSeparator)
            builder.append(parts[i].substring(1))
        else if (!endsWithSeparator && !beginsWithSeparator)
            builder.append(File.separatorChar).append(parts[i])
        else
            builder.append(parts[i])
    }
    return builder.toString()
}
