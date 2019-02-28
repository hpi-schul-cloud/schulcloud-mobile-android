package org.schulcloud.mobile.models.course

import io.realm.RealmList
import org.schulcloud.mobile.models.user.User
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object CourseSpec : Spek({
    val id = "id"
    val schoolId = "schoolId"
    val name = "name"
    val description = "description"
    val color = "#000000"
    val teachers = RealmList<User>()
    val substitutions = RealmList<User>()
    val users = RealmList<User>()

    describe("A course") {
        val course by memoized {
            Course().apply {
                this.id = id
                this.schoolId = schoolId
                this.name = name
                this.description = description
                this.color = color
                this.teachers = teachers
                this.substitutions = substitutions
                this.users = users
            }
        }

        describe("property access"){
            it("should return the assigned value"){
                assertEquals(id, course.id)
                assertEquals(schoolId, course.schoolId)
                assertEquals(name, course.name)
                assertEquals(description, course.description)
                assertEquals(color, course.color)
                assertEquals(teachers, course.teachers)
                assertEquals(substitutions, course.substitutions)
                assertEquals(users, course.users)
            }
        }
    }
})
