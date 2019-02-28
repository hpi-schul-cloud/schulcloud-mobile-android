package org.schulcloud.mobile.models.homework

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import io.realm.RealmList
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import org.joda.time.DateTimeUtils.getInstantMillis
import org.schulcloud.mobile.models.base.RealmString
import org.schulcloud.mobile.models.user.UserRepository
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.*

object HomeworkSpec : Spek({
    val id = "id"
    val teacherId = "teacherId"
    val substitutionTeacherId = "substitutionTeacherId"
    val title = "title"
    val description = "description"
    val dueDate = "dueDate"
    val course = HomeworkCourse()
    val courseWithSubstitutionTeacher = HomeworkCourse().apply {
        substitutionIds = RealmList(RealmString(substitutionTeacherId))
    }
    val restricted = true
    val publicSubmissions = true
    val invalidDueDate = "invDueDate"
    val validDueDate = "2020-07-12T10:10:10.001Z"
    val userId = "userId"
    val dateTime = DateTime(2020, 7, 10, 9, 10, 10, 1)
    val dueTimespanDays = 2
    val dueTimespanHours = 49
    val isTeacherCases = mapOf(course to mapOf(userId to false,
                                                teacherId to true,
                                                substitutionTeacherId to false),
                                courseWithSubstitutionTeacher to mapOf(userId to false,
                                                                        teacherId to true,
                                                                        substitutionTeacherId to true))
    val submissionVisibilityCases = mapOf(false to mapOf(userId to mapOf(true to true, false to false),
                                                            teacherId to mapOf(true to true, false to true)),
                                            true to mapOf(userId to mapOf(true to false, false to false),
                                                            teacherId to mapOf(true to false, false to false)))

    describe("A homework") {
        val homework by memoized {
            Homework().apply {
                this.id = id
                this.teacherId = teacherId
                this.title = title
                this.description = description
                this.dueDate = dueDate
                this.course = course
                this.restricted = restricted
                this.publicSubmissions = publicSubmissions
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(id, homework.id)
                assertEquals(teacherId, homework.teacherId)
                assertEquals(title, homework.title)
                assertEquals(description, homework.description)
                assertEquals(dueDate, homework.dueDate)
                assertEquals(course, homework.course)
                assertEquals(restricted, homework.restricted)
                assertEquals(publicSubmissions, homework.publicSubmissions)
            }
        }

        describe("setting valid dueDate") {
            beforeEach {
                homework.dueDate = validDueDate
                DateTimeUtils.setCurrentMillisFixed(getInstantMillis(dateTime))
            }

            afterEach {
                DateTimeUtils.setCurrentMillisSystem()
            }

            it("dueDateTime should not be null") {
                assertNotNull(homework.dueDateTime)
            }

            it("timespans should be correct") {
                assertEquals(dueTimespanHours, homework.dueTimespanHours)
                assertEquals(dueTimespanDays, homework.dueTimespanDays)
            }
        }

        describe("setting invalid dueDate") {
            beforeEach {
                homework.dueDate = invalidDueDate
            }

            it("dueDateTime should be null") {
                assertNull(homework.dueDateTime)
            }

            it("timespans should be null") {
                assertNull(homework.dueTimespanHours)
                assertNull(homework.dueTimespanDays)
            }
        }

        describe("checking if user is teacher") {
            isTeacherCases.forEach { homeworkCourse, isTeacherCasesForCourse ->
                describe("homework belongs to course " +
                        "${if (homeworkCourse.substitutionIds?.size ?: 0 > 0) "with" else "without"} substitution teacher") {
                    beforeEach {
                        homework.course = homeworkCourse
                    }

                    isTeacherCasesForCourse.forEach { user, expectedIsTeacher ->
                        describe("user is $user") {
                            it("isTeacher should be $expectedIsTeacher") {
                                assertEquals(expectedIsTeacher, homework.isTeacher(user))
                            }
                        }
                    }
                }
            }
        }

        describe("checking if user can see submissions") {
            mockkStatic(UserRepository::class)

            submissionVisibilityCases.forEach { restrictionState, submissionVisibilityCasesForRestrictionState ->
                describe("setting restricted to $restrictionState") {
                    beforeEach {
                        homework.restricted = restrictionState
                    }

                    submissionVisibilityCasesForRestrictionState.forEach { user, submissionVisibilityCasesForUsers ->
                        describe("current user is $user") {
                            beforeEach {
                                every { UserRepository.userId } returns user
                            }

                            afterEach {
                                clearAllMocks()
                            }

                            submissionVisibilityCasesForUsers.forEach { publicSubmissionsState, expectedCanSeeSubmissions ->
                                describe("setting publicSubmissions to $publicSubmissionsState") {
                                    beforeEach {
                                        homework.publicSubmissions = publicSubmissionsState
                                    }

                                    it("canSeeSubmissions should be $expectedCanSeeSubmissions") {
                                        assertEquals(expectedCanSeeSubmissions, homework.canSeeSubmissions())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
})
