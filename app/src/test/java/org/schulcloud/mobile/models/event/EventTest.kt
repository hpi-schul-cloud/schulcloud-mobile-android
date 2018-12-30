package org.schulcloud.mobile.models.event

import org.junit.Before

import org.junit.Assert.*
import io.realm.RealmList
import org.junit.Test

class EventTest {
    private companion object {
        val ID = "id"
        val TYPE = "type"
        val TITLE = "title"
        val ALLDAY = false
        val START = 1L
        val END = 2L
        val SUMMARY = "summary"
        val LOCATION = "location"
        val DESCRIPTION = "description"
        val INCLUDED = RealmList<Included>()
        val COURSEID = "course"
        val DURATION = 1L

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