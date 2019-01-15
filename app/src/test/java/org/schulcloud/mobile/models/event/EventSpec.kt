package org.schulcloud.mobile.models.event

import io.realm.RealmList
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

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
    }
    // TODO: test nextStart too
})
