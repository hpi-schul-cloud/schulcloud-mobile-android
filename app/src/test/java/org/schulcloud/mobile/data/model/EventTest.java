package org.schulcloud.mobile.data.model;

import org.junit.Before;
import org.junit.Test;
import org.schulcloud.mobile.data.model.jsonApi.Included;

import io.realm.RealmList;

import static org.junit.Assert.assertEquals;

public class EventTest {
    private static final String _ID = "ID";
    private static final String TYPE = "TEST";
    private static final String TITLE = "TEST_PATH";
    private static final Boolean ALLDAY = false;
    private static final String START = "IMAGE";
    private static final String END = "TEST_THUMBNAIL";
    private static final String SUMMARY = "IMAGE";
    private static final String LOCATION = "TEST_THUMBNAIL";
    private static final RealmList<Included> INCLUDED = new RealmList<>();
    private static final String XSCCOURSEID = "COURSE";
    private static final String XSCCOURSETIMEID = "COURSEID";

    private Event event;

    @Before
    public void setUp() {
        event = createNewEvent();
    }

    @Test
    public void testGetProperties() {
        assertEquals(event._id, _ID);
        assertEquals(event.type, TYPE);
        assertEquals(event.title, TITLE);
        assertEquals(event.allDay, ALLDAY);
        assertEquals(event.start, START);
        assertEquals(event.end, END);
        assertEquals(event.summary, SUMMARY);
        assertEquals(event.location, LOCATION);
        assertEquals(event.included, INCLUDED);
        assertEquals(event.xScCourseId, XSCCOURSEID);
        assertEquals(event.xScCourseTimeId, XSCCOURSETIMEID);
    }

    public static Event createNewEvent() {
        Event event = new Event();
        event._id = _ID;
        event.type = TYPE;
        event.title = TITLE;
        event.allDay = ALLDAY;
        event.start = START;
        event.end = END;
        event.summary = SUMMARY;
        event.location = LOCATION;
        event.included = INCLUDED;
        event.xScCourseId = XSCCOURSEID;
        event.xScCourseTimeId = XSCCOURSETIMEID;

        return event;
    }
}