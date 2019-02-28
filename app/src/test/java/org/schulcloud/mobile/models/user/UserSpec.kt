package org.schulcloud.mobile.models.user

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object UserSpec : Spek({
    val id = "id"
    val firstName = "firstName"
    val lastName = "lastName"
    val email = "e@mail"
    val schoolId = "schoolId"
    val displayName = "displayName"
    val name = "firstName lastName"
    val shortName = "f. lastName"

    describe("A user") {
        val user by memoized {
            User().apply {
                this.id = id
                this.firstName = firstName
                this.lastName = lastName
                this.email = email
                this.schoolId = schoolId
                this.displayName = displayName
            }
    }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(id, user.id)
                assertEquals(firstName, user.firstName)
                assertEquals(lastName, user.lastName)
                assertEquals(email, user.email)
                assertEquals(schoolId, user.schoolId)
                assertEquals(displayName, user.displayName)
            }

            it("name should include firstName and lastName"){
                assertEquals(name, user.name)
            }

            it("shortName should include initial and lastName"){
                assertEquals(shortName, user.shortName)
            }
        }
    }
})
