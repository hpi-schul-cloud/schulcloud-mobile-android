package org.schulcloud.mobile.models.event

import java.util.*

val calendarSixDaysAgo = GregorianCalendar.getInstance().apply {
    timeInMillis = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 4, 9, 10, 1)
}
val calendarInTwoDays = GregorianCalendar.getInstance().apply {
    timeInMillis = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 12, 9, 10, 1)
}
val calendarInSevenDays = GregorianCalendar.getInstance().apply {
    timeInMillis = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 17, 9, 10, 1)
}
val calendarTomorrow = GregorianCalendar.getInstance().apply {
    timeInMillis = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 11, 9, 10, 1)
}
val calendarToday = GregorianCalendar.getInstance().apply {
    timeInMillis = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 10, 9, 10, 1)
}

fun userCalendar(): Calendar = GregorianCalendar.getInstance().apply {
    timeInMillis = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 10, 10, 10, 1)
}
fun calendar() : Calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("TIMEZONE_UTC")).apply {
    timeInMillis = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 10, 10, 10, 1)
}

fun getMillisecondsFromCalendarWithDatetimeValues(year: Int,
                                                  month: Int,
                                                  day: Int,
                                                  hourOfDay: Int,
                                                  minute: Int,
                                                  second: Int): Long = GregorianCalendar.getInstance().apply {
    clear()
    set(year, month, day, hourOfDay, minute, second)
}.timeInMillis
