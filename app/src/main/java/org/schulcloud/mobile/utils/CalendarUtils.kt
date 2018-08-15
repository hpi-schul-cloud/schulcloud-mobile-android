package org.schulcloud.mobile.utils

import android.text.format.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

val TIMEZONE_UTC: TimeZone = TimeZone.getTimeZone("TIMEZONE_UTC")
val UTC_FORMAT: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
val TIMEZONE_LOCAL: TimeZone = TimeZone.getDefault()

const val WEEK_IN_DAYS = 7
const val DAY_IN_HOURS = 24
const val HOUR_IN_MINUTES = 60
const val MINUTE_IN_SECONDS = 60

fun getCalendar(): Calendar {
    return GregorianCalendar.getInstance(TIMEZONE_UTC)
}

fun getUserCalendar(): Calendar {
    return GregorianCalendar.getInstance()
}

fun Calendar.toLocal() {
    add(Calendar.MILLISECOND, -TIMEZONE_LOCAL.getOffset(timeInMillis))
}

fun Calendar.fromLocal() {
    add(Calendar.MILLISECOND, TIMEZONE_LOCAL.getOffset(timeInMillis))
}

fun String.parseDate(): Calendar? {
    return try {
        getCalendar().also {
            it.time = UTC_FORMAT.parse(this)
        }
    } catch (e: ParseException) {
        null
    }
}

var Calendar.timeOfDay: Long
    get() = (get(Calendar.HOUR_OF_DAY).toLong() * DateUtils.HOUR_IN_MILLIS
            + get(Calendar.MINUTE) * DateUtils.MINUTE_IN_MILLIS
            + get(Calendar.SECOND) * DateUtils.SECOND_IN_MILLIS
            + get(Calendar.MILLISECOND))
    set(value) {
        var time = value
        set(Calendar.MILLISECOND, (value % DateUtils.SECOND_IN_MILLIS).toInt())
        time /= DateUtils.SECOND_IN_MILLIS
        set(Calendar.SECOND, (time % MINUTE_IN_SECONDS).toInt())
        time /= MINUTE_IN_SECONDS
        set(Calendar.MINUTE, (time % HOUR_IN_MINUTES).toInt())
        time /= HOUR_IN_MINUTES
        set(Calendar.HOUR_OF_DAY, (time % DAY_IN_HOURS).toInt())
    }

var Calendar.dayOfWeek: Int
    get() = get(Calendar.DAY_OF_WEEK)
    set(value) = set(Calendar.DAY_OF_WEEK, value)

val Calendar.isToday: Boolean
    get() = DateUtils.isToday(timeInMillis)
