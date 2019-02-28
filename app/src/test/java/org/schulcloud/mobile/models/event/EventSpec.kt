package org.schulcloud.mobile.models.event

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import io.realm.RealmList
import org.schulcloud.mobile.utils.getCalendar
import org.schulcloud.mobile.utils.getUserCalendar
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

object EventSpec : Spek({
    fun getMillisecondsFromCalendarWithDatetimeValues(year: Int,
                                                      month: Int,
                                                      day: Int,
                                                      hourOfDay: Int,
                                                      minute: Int,
                                                      second: Int): Long = GregorianCalendar.getInstance().apply {
        clear()
        set(year, month, day, hourOfDay, minute, second)
    }.timeInMillis

    val id = "id"
    val type = "type"
    val title = "title"
    val allDay = false
    val start = 1L
    val end = 2L
    val summary = "summary"
    val location = "location"
    val description = "description"
    val included = RealmList<Included>()
    val courseId = "course"
    val duration = 1L

    val includedWeeklyThursday = RealmList<Included>(Included().apply {
        attributes = IncludedAttributes().apply {
            freq = "WEEKLY"
            weekday = "TH"
        }
    })
    val includedWeeklyTuesday = RealmList<Included>(Included().apply {
        attributes = IncludedAttributes().apply {
            freq = "WEEKLY"
            weekday = "TU"
        }
    })
    val includedDaily = RealmList<Included>(Included().apply {
        attributes = IncludedAttributes().apply {
            freq = "DAILY"
            weekday = "TU"
        }
    })
   val includedUntil = RealmList<Included>(Included().apply {
        attributes = IncludedAttributes().apply {
            until = "2020-02-07'T'10:10:01.001'Z'"
        }
    })

    val startInTwoDays = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 12, 9, 10, 1)
    val startSixDaysAgo = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 4, 9, 10, 1)
    val endSixDaysAgo = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 4, 10, 20, 1)

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

    describe("An event") {
        val event by memoized {
            Event().apply {
                this.id = id
                this.type = type
                this.title = title
                this.allDay = allDay
                this.start = start
                this.end = end
                this.summary = summary
                this.location = location
                this.description = description
                this.included = included
                this.courseId = courseId
            }
        }

        mockkStatic("org.schulcloud.mobile.utils.CalendarUtilsKt")

        beforeEach {
            // TODO: ensure new computation on every call
            every { getCalendar() } returns calendar()
            every { getUserCalendar() } returns userCalendar()
        }

        afterEach {
            clearAllMocks()
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(event.id, id)
                assertEquals(event.type, type)
                assertEquals(event.title, title)
                assertEquals(event.allDay, allDay)
                assertEquals(event.start, start)
                assertEquals(event.end, end)
                assertEquals(event.summary, summary)
                assertEquals(event.location, location)
                assertEquals(event.included, included)
                assertEquals(event.courseId, courseId)
                assertEquals(event.duration, duration)
            }
        }

        // TODO: Summarize test cases
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
