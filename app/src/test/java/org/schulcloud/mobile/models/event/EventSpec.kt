package org.schulcloud.mobile.models.event

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.realm.RealmList
import org.schulcloud.mobile.utils.getCalendar
import org.schulcloud.mobile.utils.getUserCalendar
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

private const val ID = "id"
private const val TYPE = "type"
private const val TITLE = "title"
private const val ALLDAY = false
private const val START = 1L
private const val END = 2L
private const val SUMMARY = "summary"
private const val LOCATION = "location"
private const val DESCRIPTION = "description"
private val included = RealmList<Included>()
private const val COURSEID = "course"
private const val DURATION = 1L

private val includedWeeklyThursday = RealmList<Included>(Included().apply {
    attributes = IncludedAttributes().apply {
        freq = "WEEKLY"
        weekday = "TH"
    }
})
private val includedWeeklyTuesday = RealmList<Included>(Included().apply {
    attributes = IncludedAttributes().apply {
        freq = "WEEKLY"
        weekday = "TU"
    }
})
private val includedDaily = RealmList<Included>(Included().apply {
    attributes = IncludedAttributes().apply {
        freq = "DAILY"
        weekday = "TU"
    }
})
private val includedUntil = RealmList<Included>(Included().apply {
    attributes = IncludedAttributes().apply {
        until = "2020-02-07'T'10:10:01.001'Z'"
    }
})

private val startInTwoDays = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 12, 9, 10, 1)
private val startSixDaysAgo = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 4, 9, 10, 1)
private val endSixDaysAgo = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 4, 10, 20, 1)

private val calendarInTwoDays = GregorianCalendar.getInstance().apply {
    timeInMillis = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 12, 9, 10, 1)
}
private val calendarInSevenDays = GregorianCalendar.getInstance().apply {
    timeInMillis = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 17, 9, 10, 1)
}
private val calendarTomorrow = GregorianCalendar.getInstance().apply {
    timeInMillis = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 11, 9, 10, 1)
}
private val calendarToday = GregorianCalendar.getInstance().apply {
    timeInMillis = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 10, 9, 10, 1)
}

private val userCalendar: Calendar
    get() = GregorianCalendar.getInstance().apply {
        timeInMillis = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 10, 10, 10, 1)
    }
private val calendar: Calendar
    get() = GregorianCalendar.getInstance(TimeZone.getTimeZone("TIMEZONE_UTC")).apply {
        timeInMillis = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 10, 10, 10, 1)
    }

private fun getMillisecondsFromCalendarWithDatetimeValues(year: Int,
                                                          month: Int,
                                                          day: Int,
                                                          hourOfDay: Int,
                                                          minute: Int,
                                                          second: Int): Long = GregorianCalendar.getInstance().apply {
    clear()
    set(year, month, day, hourOfDay, minute, second)
}.timeInMillis

object EventSpec : Spek({
    describe("An event") {
        val event by memoized {
            Event().also {
                it.id = ID
                it.type = TYPE
                it.title = TITLE
                it.allDay = ALLDAY
                it.start = START
                it.end = END
                it.summary = SUMMARY
                it.location = LOCATION
                it.description = DESCRIPTION
                it.included = org.schulcloud.mobile.models.event.included
                it.courseId = COURSEID
            }
        }

        beforeEach {
            mockkStatic("org.schulcloud.mobile.utils.CalendarUtilsKt")
            every { getCalendar() } returns calendar
            every { getUserCalendar() } returns userCalendar
        }

        afterEach {
            unmockkStatic("org.schulcloud.mobile.utils.CalendarUtilsKt")
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(event.id, ID)
                assertEquals(event.type, TYPE)
                assertEquals(event.title, TITLE)
                assertEquals(event.allDay, ALLDAY)
                assertEquals(event.start, START)
                assertEquals(event.end, END)
                assertEquals(event.summary, SUMMARY)
                assertEquals(event.location, LOCATION)
                assertEquals(event.included, included)
                assertEquals(event.courseId, COURSEID)
                assertEquals(event.duration, DURATION)
            }
        }

        describe("setting start in future") {
            beforeEach {
                event.start = startInTwoDays
            }

            describe("setting no frequency") {
                beforeEach {
                    event.included = null
                }

                it("next start should be on the day of start") {
                    assertEquals(calendarInTwoDays, event.nextStart())
                }
            }

            describe("setting weekly frequency") {
                beforeEach {
                    event.included = includedWeeklyThursday
                }

                it("next start should be on the day of start") {
                    assertEquals(calendarInTwoDays, event.nextStart())
                }
            }

            describe("setting daily frequency") {
                beforeEach {
                    event.included = includedDaily
                }

                it("next start should be on the day of start") {
                    assertEquals(calendarInTwoDays, event.nextStart())
                }
            }
        }

        describe("setting start in past") {
            beforeEach {
                event.start = startSixDaysAgo
            }

            describe("setting until in past") {
                beforeEach {
                    event.included = includedUntil
                }

                it("there should be no next start") {
                    assertNull(event.nextStart())
                }
            }

            describe("setting no frequency") {
                beforeEach {
                    event.included = null
                }

                it("there should be no next start") {
                    assertNull(event.nextStart())
                }
            }

            describe("setting weekly frequency") {
                beforeEach {
                    event.included = includedWeeklyThursday
                }

                it("next start should be on the next relevant weekday") {
                    assertEquals(calendarInTwoDays, event.nextStart())
                }
            }

            describe("setting daily frequency") {
                beforeEach {
                    event.included = includedDaily
                }

                it("next start should be tomorrow") {
                    assertEquals(calendarTomorrow, event.nextStart())
                }
            }
        }

        describe("setting start and end in past with duration including current time of day"){
            beforeEach {
                event.start = startSixDaysAgo
                event.end = endSixDaysAgo
            }

            describe("setting until in past") {
                beforeEach {
                    event.included = includedUntil
                }

                it("there should be no next start") {
                    assertNull(event.nextStart())
                }
            }

            describe("setting no frequency") {
                beforeEach {
                    event.included = null
                }

                it("there should be no next start") {
                    assertNull(event.nextStart())
                    assertNull(event.nextStart(true))
                }
            }

            describe("setting weekly frequency") {
                beforeEach {
                    event.included = includedWeeklyTuesday
                }

                it("next start should depend on includeCurrent") {
                    assertEquals(calendarInSevenDays, event.nextStart())
                    assertEquals(calendarToday, event.nextStart(true))
                }
            }

            describe("setting daily frequency") {
                beforeEach {
                    event.included = includedDaily
                }

                it("next start should depend on includeCurrent") {
                    assertEquals(calendarTomorrow, event.nextStart())
                    assertEquals(calendarToday, event.nextStart(true))
                }
            }
        }
    }
})
