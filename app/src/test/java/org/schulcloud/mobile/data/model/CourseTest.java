package org.schulcloud.mobile.data.model;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class CourseTest {
    private static final String ID = "ID";
    private static final String SCHOOLID = "schoolId";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String COLOR = "#00000";

    private Course course;

    @Before
    public void setUp() throws Exception {
        course = createNewCourse();
    }

    @Test
    public void testGetProperties() {
        assertEquals(course._id, ID);
        assertEquals(course.schoolId, SCHOOLID);
        assertEquals(course.name, NAME);
        assertEquals(course.description, DESCRIPTION);
        assertEquals(course.color, COLOR);
    }

    public static Course createNewCourse() {
        Course c = new Course();
        c._id = ID;
        c.schoolId = SCHOOLID;
        c.name = NAME;
        c.description = DESCRIPTION;
        c.color = COLOR;

        return c;
    }

}