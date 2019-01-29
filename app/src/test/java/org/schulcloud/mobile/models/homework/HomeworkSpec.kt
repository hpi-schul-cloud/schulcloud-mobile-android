package org.schulcloud.mobile.models.homework

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.realm.RealmList
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import org.joda.time.DateTimeUtils.getInstantMillis
import org.schulcloud.mobile.models.base.RealmString
import org.schulcloud.mobile.models.user.UserRepository
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.*

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
private const val USERID = "UserId"
private val FAKE_DATE = DateTime(2020, 7, 10, 9, 10, 10, 1)
private const val DUETIMESPAN_DAYS = 2
private const val DUETIMESPAN_HOURS = 49
private const val USERID_TEACHER_OTHER = "teacherIdOther"
private val COURSE_SUBSTITUTION_TEACHER = HomeworkCourse().apply {
    substitutionIds = RealmList(RealmString("teacherIdOther"))
}

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
                DateTimeUtils.setCurrentMillisFixed(getInstantMillis(FAKE_DATE))
            }

            it("dueDateTime should not be null") {
                assertNotNull(homework.dueDateTime)
            }

            it("timespans should be correct") {
                assertEquals(DUETIMESPAN_HOURS, homework.dueTimespanHours)
                assertEquals(DUETIMESPAN_DAYS, homework.dueTimespanDays)
            }

            afterEach {
                DateTimeUtils.setCurrentMillisSystem()
            }
        }

        describe("setting invalid dueDate") {
            beforeEach {
                homework.dueDate = INVALID_DUEDATE
            }

            it("dueDateTime should be null") {
                assertNull(homework.dueDateTime)
            }

            it("timespans should be null") {
                assertNull(homework.dueTimespanHours)
                assertNull(homework.dueTimespanDays)
            }
        }

        describe("current user is teacher") {
            it("isTeacher should be true") {
                assertTrue(homework.isTeacher(TEACHERID))
            }
        }

        describe("setting homework not restricted") {
            beforeEach {
                mockkStatic(UserRepository::class)
                every { UserRepository.userId } returns USERID
                homework.restricted = false
            }

            describe("setting submissions public") {
                beforeEach {
                    homework.publicSubmissions = true
                }
                it("user should be able to see submissions") {
                    assertTrue(homework.canSeeSubmissions())
                }
            }

            describe("setting submissions not public") {
                beforeEach {
                    homework.publicSubmissions = false
                }
                it("user should not be able to see submissions") {
                    assertFalse(homework.canSeeSubmissions())
                }
            }

            afterEach {
                unmockkStatic(UserRepository::class)
            }
        }
    }
})
