/*
package org.schulcloud.mobile.models.homework

import io.realm.RealmList
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.schulcloud.mobile.models.base.RealmString

class HomeworkTest {
    private companion object {
        const val ID = "id"
        const val TEACHERID = "teacherId"
        const val TITLE = "title"
        const val DESCRIPTION = "description"
        const val DUEDATE = "dueDate"
        val COURSE = HomeworkCourse()
        const val RESTRICTED = true
        const val PUBLICSUBMISSIONS = true
        const val INVALID_DUEDATE = "invDueDate"
        const val VALID_DUEDATE = "2020-07-12T10:10:10.001Z"
        const val USERID_TEACHER = "teacherId"
        const val USERID_TEACHER_OTHER = "teacherIdOther"
        val COURSE_SUBSTITUTION_TEACHER = HomeworkCourse().apply {
            substitutionIds = RealmList(RealmString("teacherIdOther"))
        }
        */
/*val DUEDATE_DAY_DIFFERENCE = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .print(LocalDateTime.now().plusDays(2))
        val DUEDATE_HOUR_DIFFERENCE = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .print(LocalDateTime.now().plusHours(3).plusMinutes(1))*//*

        const val DUETIMESPANDAYS = 2
        const val DUETIMESPANHOURS = 3
    }

    private val newHomework: Homework
        get() = Homework().apply {
            id = ID
            teacherId = TEACHERID
            title = TITLE
            description = DESCRIPTION
            dueDate = DUEDATE
            course = COURSE
            restricted = RESTRICTED
            publicSubmissions = PUBLICSUBMISSIONS
        }
    private lateinit var homework: Homework

    @Before
    fun setUp() {
        homework = newHomework
    }

    @Test
    fun testGetProperties() {
        assertEquals(ID, homework.id)
        assertEquals(TEACHERID, homework.teacherId)
        assertEquals(TITLE, homework.title)
        assertEquals(DESCRIPTION, homework.description)
        assertEquals(DUEDATE, homework.dueDate)
        assertEquals(COURSE, homework.course)
        assertEquals(RESTRICTED, homework.restricted)
        assertEquals(PUBLICSUBMISSIONS, homework.publicSubmissions)
    }

    @Test
    fun testDatePropertiesNullWhenDuedateInvalid() {
        homework.dueDate = INVALID_DUEDATE
        assertNull(homework.dueDateTime)
        assertNull(homework.dueTimespanDays)
        assertNull(homework.dueTimespanHours)
    }

    @Test
    fun testDatetimeNotNullWhenDuedateValid() {
        homework.dueDate = VALID_DUEDATE
        assertNotNull(homework.dueDateTime)
    }

   */
/* @Test
    fun testDuetimespanCorrect() {
        homework.dueDate = DUEDATE_DAY_DIFFERENCE
        assertEquals(DUETIMESPANDAYS, homework.dueTimespanDays)
        homework.dueDate = DUEDATE_HOUR_DIFFERENCE
        assertEquals(DUETIMESPANHOURS, homework.dueTimespanHours)
    }*//*


    @Test
    fun testIsteacherFalseWhenUserAndCourseSubstitutionsNotTeacherid(){
        assertFalse(homework.isTeacher(""))
    }

    @Test
    fun testIsteacherTrueWhenUserOrCourseSubstitutionTeacherid(){
        assertTrue(homework.isTeacher(USERID_TEACHER))
        homework.course = COURSE_SUBSTITUTION_TEACHER
        // TODO: check if that is really the required behaviour
        assertTrue(homework.isTeacher(USERID_TEACHER_OTHER))
    }

    // TODO: add tests for canSeeSubmissions
}
*/
