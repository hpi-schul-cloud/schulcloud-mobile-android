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
private val INCLUDED = RealmList<Included>()
private const val COURSEID = "course"
private const val DURATION = 1L

private val INCLUDED_WEEKLY_THURSDAY = RealmList<Included>(Included().apply {
    attributes = IncludedAttributes().apply {
        freq = "WEEKLY"
        weekday = "TH"
    }
})
private val INCLUDED_WEEKLY_TUESDAY = RealmList<Included>(Included().apply {
    attributes = IncludedAttributes().apply {
        freq = "WEEKLY"
        weekday = "TU"
    }
})
private val INCLUDED_DAILY = RealmList<Included>(Included().apply {
    attributes = IncludedAttributes().apply {
        freq = "DAILY"
        weekday = "TU"
    }
})
private val INCLUDED_UNTIL = RealmList<Included>(Included().apply {
    attributes = IncludedAttributes().apply {
        until = "2020-02-07'T'10:10:01.001'Z'"
    }
})

private val START_IN_TWO_DAYS = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 12, 9, 10, 1)
private val START_SIX_DAYS_AGO = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 4, 9, 10, 1)
private val END_SIX_DAYS_AGO = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 4, 10, 20, 1)

private val CALENDAR_IN_TWO_DAYS = GregorianCalendar.getInstance().apply {
    timeInMillis = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 12, 9, 10, 1)
}
private val CALENDAR_IN_SEVEN_DAYS = GregorianCalendar.getInstance().apply {
    timeInMillis = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 17, 9, 10, 1)
}
private val CALENDAR_TOMORROW = GregorianCalendar.getInstance().apply {
    timeInMillis = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 11, 9, 10, 1)
}
private val CALENDAR_TODAY = GregorianCalendar.getInstance().apply {
    timeInMillis = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 10, 9, 10, 1)
}

private val FAKE_USER_CALENDAR: Calendar
    get() = GregorianCalendar.getInstance().apply {
        timeInMillis = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 10, 10, 10, 1)
    }
private val FAKE_CALENDAR: Calendar
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
            Event().apply {
                id = ID
                type = TYPE
                title = TITLE
                allDay = ALLDAY
                start = START
                end = END
                summary = SUMMARY
                location = LOCATION
                description = DESCRIPTION
                included = INCLUDED
                courseId = COURSEID
            }
        }

        beforeEach {
            mockkStatic("org.schulcloud.mobile.utils.CalendarUtilsKt")
            every { getCalendar() } returns FAKE_CALENDAR
            every { getUserCalendar() } returns FAKE_USER_CALENDAR
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
                assertEquals(event.included, INCLUDED)
                assertEquals(event.courseId, COURSEID)
                assertEquals(event.duration, DURATION)
            }
        }

        describe("setting start in future") {
            beforeEach {
                event.start = START_IN_TWO_DAYS
            }

            describe("setting no frequency") {
                beforeEach {
                    event.included = null
                }

                it("next start should be on the day of start") {
                    assertEquals(CALENDAR_IN_TWO_DAYS, event.nextStart())
                }
            }

            describe("setting weekly frequency") {
                beforeEach {
                    event.included = INCLUDED_WEEKLY_THURSDAY
                }

                it("next start should be on the day of start") {
                    assertEquals(CALENDAR_IN_TWO_DAYS, event.nextStart())
                }
            }

            describe("setting daily frequency") {
                beforeEach {
                    event.included = INCLUDED_DAILY
                }

                it("next start should be on the day of start") {
                    assertEquals(CALENDAR_IN_TWO_DAYS, event.nextStart())
                }
            }
        }

        describe("setting start in past") {
            beforeEach {
                event.start = START_SIX_DAYS_AGO
            }

            describe("setting until in past") {
                beforeEach {
                    event.included = INCLUDED_UNTIL
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
                    event.included = INCLUDED_WEEKLY_THURSDAY
                }

                it("next start should be on the next relevant weekday") {
                    assertEquals(CALENDAR_IN_TWO_DAYS, event.nextStart())
                }
            }

            describe("setting daily frequency") {
                beforeEach {
                    event.included = INCLUDED_DAILY
                }

                it("next start should be tomorrow") {
                    assertEquals(CALENDAR_TOMORROW, event.nextStart())
                }
            }
        }

        describe("setting start and end in past with duration including current time of day"){
            beforeEach {
                event.start = START_SIX_DAYS_AGO
                event.end = END_SIX_DAYS_AGO
            }

            describe("setting until in past") {
                beforeEach {
                    event.included = INCLUDED_UNTIL
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
                    event.included = INCLUDED_WEEKLY_TUESDAY
                }

                it("next start should depend on includeCurrent") {
                    assertEquals(CALENDAR_IN_SEVEN_DAYS, event.nextStart())
                    assertEquals(CALENDAR_TODAY, event.nextStart(true))
                }
            }

            describe("setting daily frequency") {
                beforeEach {
                    event.included = INCLUDED_DAILY
                }

                it("next start should depend on includeCurrent") {
                    assertEquals(CALENDAR_TOMORROW, event.nextStart())
                    assertEquals(CALENDAR_TODAY, event.nextStart(true))
                }
            }
        }
    }
})
