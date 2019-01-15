package org.schulcloud.mobile.models.homework

import io.realm.RealmList
import org.schulcloud.mobile.models.base.RealmString
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private const val ID = "id"
private const val TEACHERID = "teacherId"
private const val TITLE = "title"
private const val DESCRIPTION = "description"
private const val DUEDATE = "dueDate"
private val COURSE = HomeworkCourse()
private const val RESTRICTED = true
private const val PUBLICSUBMISSIONS = true
private const val INVALID_DUEDATE = "invDueDate"
private const val VALID_DUEDATE = "2020-07-12T10:10:10.001Z"
private const val USERID_TEACHER = "teacherId"
private const val USERID_TEACHER_OTHER = "teacherIdOther"
private val COURSE_SUBSTITUTION_TEACHER = HomeworkCourse().apply {
    substitutionIds = RealmList(RealmString("teacherIdOther"))
}
/*val DUEDATE_DAY_DIFFERENCE = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .print(LocalDateTime.now().plusDays(2))
        val DUEDATE_HOUR_DIFFERENCE = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .print(LocalDateTime.now().plusHours(3).plusMinutes(1))*//*

        const val DUETIMESPANDAYS = 2
        const val DUETIMESPANHOURS = 3*/

object HomeworkSpec : Spek({
    describe("A homework") {
        val homework by memoized {
            Homework().apply {
                id = ID
                teacherId = TEACHERID
                title = TITLE
                description = DESCRIPTION
                dueDate = DUEDATE
                course = COURSE
                restricted = RESTRICTED
                publicSubmissions = PUBLICSUBMISSIONS
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(ID, homework.id)
                assertEquals(TEACHERID, homework.teacherId)
                assertEquals(TITLE, homework.title)
                assertEquals(DESCRIPTION, homework.description)
                assertEquals(DUEDATE, homework.dueDate)
                assertEquals(COURSE, homework.course)
                assertEquals(RESTRICTED, homework.restricted)
                assertEquals(PUBLICSUBMISSIONS, homework.publicSubmissions)
            }
        }

        describe("setting valid dueDate") {
            beforeEach {
                homework.dueDate = VALID_DUEDATE
            }

            it("dueDateTime should not be null") {
                assertNotNull(homework.dueDateTime)
            }
        }

        describe("setting invalid dueDate") {
            beforeEach {
                homework.dueDate = INVALID_DUEDATE
            }

            it("dueDateTime should be null") {
                assertNull(homework.dueDateTime)
            }
        }
        // TODO: add tests for dueTimespans, isTeacher, canSeeSubmissions, mock context for jodatime
    }
})
