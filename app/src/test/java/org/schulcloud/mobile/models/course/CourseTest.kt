package org.schulcloud.mobile.models.course

import io.realm.RealmList
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.schulcloud.mobile.models.user.User

class CourseTest {
    private companion object {
        const val ID = "id"
        const val SCHOOLID = "schoolId"
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val COLOR = "#000000"
        val TEACHERS = RealmList<User>()
        val SUBSTITUTIONS = RealmList<User>()
        val USERS = RealmList<User>()
    }

    private val newCourse: Course
        get() = Course().apply {
            id = ID
            schoolId = SCHOOLID
            name = NAME
            description = DESCRIPTION
            color = COLOR
            teachers = TEACHERS
            substitutions = SUBSTITUTIONS
            users = USERS
        }
    private lateinit var course: Course

    @Before
    fun setUp() {
        course = newCourse
    }

    @Test
    fun testGetProperties() {
        assertEquals(ID, course.id)
        assertEquals(SCHOOLID, course.schoolId)
        assertEquals(NAME, course.name)
        assertEquals(DESCRIPTION, course.description)
        assertEquals(COLOR, course.color)
        assertEquals(TEACHERS, course.teachers)
        assertEquals(SUBSTITUTIONS, course.substitutions)
        assertEquals(USERS, course.users)
    }
}
