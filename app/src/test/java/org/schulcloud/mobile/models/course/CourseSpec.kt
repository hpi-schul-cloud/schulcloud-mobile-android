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
private val TEACHERS = RealmList<User>()
private val SUBSTITUTIONS = RealmList<User>()
private val USERS = RealmList<User>()

object CourseSpec : Spek({
    describe("A course") {
        val course by memoized {
            Course().apply {
                id = ID
                schoolId = SCHOOLID
                name = NAME
                description = DESCRIPTION
                color = COLOR
                teachers = TEACHERS
                substitutions = SUBSTITUTIONS
                users = USERS
            }
        }

        describe("Property access"){
            it("should return the correct value"){
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
    }
})
