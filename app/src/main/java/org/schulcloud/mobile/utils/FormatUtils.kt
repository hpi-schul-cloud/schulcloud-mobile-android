package org.schulcloud.mobile.utils

import android.content.Context
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.schulcloud.mobile.R
import java.text.DecimalFormat

/**
 * Date: 7/12/2018
 */

private val UNITS = arrayOf("B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")

fun Long.formatFileSize(): String {
    if (this <= 0)
        return "0"
    val digitGroups = (Math.log10(toDouble()) / Math.log10(1024.0)).toInt()

    return "${DecimalFormat("#,##0.#").format(this / Math.pow(1024.0, digitGroups.toDouble()))} ${UNITS[digitGroups]}"
}

fun DateTime?.formatDaysLeft(context: Context): String {
    return if (this == null)
        context.getString(R.string.homework_error_invalidDueDate)
    else when (Days.daysBetween(LocalDateTime.now(), toLocalDateTime()).days) {
        -1 -> context.getString(R.string.general_date_yesterday)
        0 -> context.getString(R.string.general_date_today)
        1 -> context.getString(R.string.general_date_tomorrow)
        else -> DateTimeFormat.mediumDate().print(this)
    }
}
