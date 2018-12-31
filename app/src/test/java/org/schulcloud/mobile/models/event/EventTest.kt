package org.schulcloud.mobile.models.event

import org.junit.Before

import org.junit.Assert.*
import io.realm.RealmList
import org.junit.Test

class EventTest {
    private companion object {
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

    }

    private val newEvent: Event
        get() = Event().apply {
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

    private lateinit var event: Event

    @Before
    fun setUp() {
        event = newEvent
    }

    @Test
    fun testGetProperties() {
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
    // TODO: test nextStart too
}
