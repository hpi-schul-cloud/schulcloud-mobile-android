package org.schulcloud.mobile.models.event

import io.realm.RealmList
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

const val ID = "id"
const val TYPE = "type"
const val TITLE = "title"
const val ALLDAY = false
const val START = 1L
const val END = 2L
const val SUMMARY = "summary"
const val LOCATION = "location"
const val DESCRIPTION = "description"
val INCLUDED = RealmList<Included>()
const val COURSEID = "course"
const val DURATION = 1L

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


        describe("Property access") {
            it("should return the correct value") {
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
