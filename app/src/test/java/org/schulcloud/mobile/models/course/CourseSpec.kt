package org.schulcloud.mobile.models.course

import io.realm.RealmList
import org.schulcloud.mobile.models.user.User
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private const val ID = "id"
private const val SCHOOLID = "schoolId"
private const val NAME = "name"
private const val DESCRIPTION = "description"
private const val COLOR = "#000000"
private val teachers = RealmList<User>()
private val substitutions = RealmList<User>()
private val users = RealmList<User>()

object CourseSpec : Spek({
    describe("A course") {
        val course by memoized {
            Course().also {
                it.id = ID
                it.schoolId = SCHOOLID
                it.name = NAME
                it.description = DESCRIPTION
                it.color = COLOR
                it.teachers = teachers
                it.substitutions = substitutions
                it.users = users
            }
        }

        describe("property access"){
            it("should return the assigned value"){
                assertEquals(ID, course.id)
                assertEquals(SCHOOLID, course.schoolId)
                assertEquals(NAME, course.name)
                assertEquals(DESCRIPTION, course.description)
                assertEquals(COLOR, course.color)
                assertEquals(teachers, course.teachers)
                assertEquals(substitutions, course.substitutions)
                assertEquals(users, course.users)
            }
        }
    }
})
