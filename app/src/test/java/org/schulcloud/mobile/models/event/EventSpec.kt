package org.schulcloud.mobile.models.event

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import io.realm.RealmList
import org.schulcloud.mobile.utils.getCalendar
import org.schulcloud.mobile.utils.getUserCalendar
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object EventSpec : Spek({
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

    val inTwoDays = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 12, 9, 10, 1)
    val startSixDaysAgo = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 4, 9, 10, 1)
    val endSixDaysAgo = getMillisecondsFromCalendarWithDatetimeValues(2020, 2, 4, 10, 20, 1)

    val noFreqText = "no"
    val untilPastNoFreqText = "until in past with no"
    val dayOfStartText = "on the day and time of start"
    val neverText = "never"
    val tomorrowText = "tomorrow"
    val nextRelWeekdayText = "on the next relevant weekday"
    val dependingIncludeCurrentText = "depending on includeCurrent"

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
            until = "2020-02-07T10:10:01.001Z"
        }
    })

    val nextStartCases = mapOf(inTwoDays to mapOf(null to Triple(noFreqText, dayOfStartText, calendarInTwoDays),
                    includedWeeklyThursday to Triple(IncludedAttributes.FREQ_WEEKLY, dayOfStartText, calendarInTwoDays),
                    includedDaily to Triple(IncludedAttributes.FREQ_DAILY, dayOfStartText, calendarInTwoDays),
                    includedUntil to Triple(untilPastNoFreqText, neverText, null)),
            startSixDaysAgo to mapOf(null to Triple(noFreqText, neverText, null),
                    includedWeeklyThursday to Triple(IncludedAttributes.FREQ_WEEKLY, nextRelWeekdayText, calendarInTwoDays),
                    includedDaily to Triple(IncludedAttributes.FREQ_DAILY, tomorrowText, calendarTomorrow),
                    includedUntil to Triple(untilPastNoFreqText, neverText, null)))

    val nextStartCasesWithStartAndEndInPast = mapOf(null to Triple(noFreqText, neverText, Pair(null, null)),
            includedWeeklyTuesday to Triple(IncludedAttributes.FREQ_WEEKLY, dependingIncludeCurrentText, Pair(calendarInSevenDays, calendarToday)),
            includedDaily to Triple(IncludedAttributes.FREQ_DAILY, dependingIncludeCurrentText, Pair(calendarTomorrow, calendarToday)),
            includedUntil to Triple(untilPastNoFreqText, neverText, Pair(null, null)))

    val nextStartCasesWithStartPastAndEndFuture = mapOf(null to Triple(noFreqText, dayOfStartText, calendarSixDaysAgo),
                    includedWeeklyThursday to Triple(IncludedAttributes.FREQ_WEEKLY, dayOfStartText, calendarSixDaysAgo),
                    includedDaily to Triple(IncludedAttributes.FREQ_DAILY, dayOfStartText, calendarSixDaysAgo),
                    includedUntil to Triple(untilPastNoFreqText, neverText, null))

    fun relativeTimeLabel(timeInMillis: Long): String {
        return when {
            timeInMillis < userCalendar().timeInMillis -> "past"
            timeInMillis == userCalendar().timeInMillis -> "present"
            else -> "future"
        }
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
            every { getCalendar() } answers { calendar() }
            every { getUserCalendar() } answers { userCalendar() }
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

        nextStartCases.forEach { start, nextStartCasesForStart -> 
            describe("setting start in ${relativeTimeLabel(start)}"){
                beforeEach { 
                    event.start = start
                }
                
                nextStartCasesForStart.forEach { included, freqTextAndNextStartTextAndNextStart ->
                    val freqText = freqTextAndNextStartTextAndNextStart.first
                    val nextStartText = freqTextAndNextStartTextAndNextStart.second
                    val nextStart = freqTextAndNextStartTextAndNextStart.third

                    describe("setting $freqText frequency"){
                        beforeEach {
                            event.included = included
                        }

                        it ("nextStart should be $nextStartText"){
                            assertEquals(nextStart, event.nextStart())
                            assertEquals(nextStart, event.nextStart(true))
                        }
                    }
                }
            }
        }

        describe("setting start and end in past with duration including current time of day") {
            beforeEach {
                event.start = startSixDaysAgo
                event.end = endSixDaysAgo
            }

            nextStartCasesWithStartAndEndInPast.forEach { included, freqTextAndNextStartTextAndNextStarts ->
                val freqText = freqTextAndNextStartTextAndNextStarts.first
                val nextStartText = freqTextAndNextStartTextAndNextStarts.second
                val nextStartWithoutIncludeCurrent = freqTextAndNextStartTextAndNextStarts.third.first
                val nextStartWithIncludeCurrent = freqTextAndNextStartTextAndNextStarts.third.second

                describe("setting $freqText frequency"){
                    beforeEach {
                        event.included = included
                    }

                    it ("nextStart should be $nextStartText"){
                        assertEquals(nextStartWithoutIncludeCurrent, event.nextStart())
                        assertEquals(nextStartWithIncludeCurrent, event.nextStart(true))
                    }
                }
            }

            describe("setting start in past and end in future") {
                beforeEach {
                    event.start = startSixDaysAgo
                    event.end = inTwoDays
                }

                nextStartCasesWithStartPastAndEndFuture.forEach { included, freqTextAndNextStartTextAndNextStarts ->
                    val freqText = freqTextAndNextStartTextAndNextStarts.first
                    val nextStartText = freqTextAndNextStartTextAndNextStarts.second
                    val nextStart = freqTextAndNextStartTextAndNextStarts.third

                    describe("setting $freqText frequency") {
                        beforeEach {
                            event.included = included
                        }

                        it("nextStart should be $nextStartText") {
                            assertEquals(nextStart, event.nextStart())
                            assertEquals(nextStart, event.nextStart(true))
                        }
                    }
                }
            }
        }
    }
})
