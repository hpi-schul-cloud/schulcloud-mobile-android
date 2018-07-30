package org.schulcloud.mobile.utils

import android.graphics.Color
import org.schulcloud.mobile.R
import org.schulcloud.mobile.SchulCloudApp
import org.schulcloud.mobile.models.homework.Homework

/**
 * Returns text and text color for the duetill label of a Homework object
 * depending on how many days and hours are left until its dueDate.
 *
 * When there is more than a week left, this function returns an empty String
 *
 * @return a Pair with duetill label text and color
 */
fun getDueTextAndColorId(homework: Homework?): Pair<String, Int> {
    val days: Int = homework?.dueTimespanDays ?: Int.MAX_VALUE - 1

    return when (days) {
        in Int.MIN_VALUE until 0 ->
            Pair(SchulCloudApp.instance.resources.getString(R.string.homework_due_outdated), Color.RED)
        0 -> {
            val hours = homework?.dueTimespanHours ?: Int.MAX_VALUE - 1
            if (hours >= 0)
                Pair(SchulCloudApp.instance.resources.getString(R.string.homework_due_inHours), Color.RED)
             else
                Pair(SchulCloudApp.instance.resources.getString(R.string.homework_due_outdated), Color.RED)
        }
        1 -> Pair(SchulCloudApp.instance.resources.getString(R.string.homework_due_tomorrow), Color.RED)
        2 -> Pair(SchulCloudApp.instance.resources.getString(R.string.homework_due_dayAfterTomorrow), Color.BLACK)
        in 3..7 -> Pair(SchulCloudApp.instance.resources.getString(R.string.homework_due_inDays), Color.BLACK)
        Int.MAX_VALUE -> Pair(SchulCloudApp.instance.resources.getString(R.string.homework_error_invalidDueDate), Color.BLACK)
        else -> Pair("", Color.TRANSPARENT)
    }
}

fun dueLabelFlagRequired(homework: Homework?): Boolean {
    homework?.let {
        return (it.dueTimespanDays <= 1)
    }
    return false
}
