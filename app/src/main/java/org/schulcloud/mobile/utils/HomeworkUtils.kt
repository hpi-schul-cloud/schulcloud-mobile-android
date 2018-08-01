package org.schulcloud.mobile.utils

import android.graphics.Color
import org.schulcloud.mobile.R
import org.schulcloud.mobile.SchulCloudApp
import org.schulcloud.mobile.models.homework.Homework

/**
 * Returns text for the duetill label of a homework
 * depending on how many days and hours are left until its dueDate.
 *
 * When there is more than a week left, this function returns an empty String
 */
fun getDueText(homework: Homework?): String {
    val days = homework?.dueTimespanDays
    return when (days) {
        null -> SchulCloudApp.instance.getString(R.string.homework_error_invalidDueDate)
        in Int.MIN_VALUE until 0 -> SchulCloudApp.instance.getString(R.string.homework_due_outdated)
        0 ->
            if (homework.dueTimespanHours ?: Int.MAX_VALUE >= 0)
                SchulCloudApp.instance.getString(R.string.homework_due_hours)
            else
                SchulCloudApp.instance.getString(R.string.homework_due_outdated)
        in 1..7 -> SchulCloudApp.instance.resources.getQuantityString(R.plurals.homework_due_days, days, days)
        else -> ""
    }
}

fun getDueColor(homework: Homework?): Int {
    return when (homework?.dueTimespanDays) {
        null -> Color.BLACK
        in Int.MIN_VALUE..1 -> Color.RED
        in 2..7 -> Color.BLACK
        else -> Color.TRANSPARENT
    }
}

fun dueLabelFlagRequired(homework: Homework?): Boolean {
    return homework?.dueTimespanDays ?: 2 <= 1
}
