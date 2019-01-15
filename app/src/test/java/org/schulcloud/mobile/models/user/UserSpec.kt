package org.schulcloud.mobile.models.user

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private const val ID = "id"
private const val FIRSTNAME = "firstName"
private const val LASTNAME = "lastName"
private const val EMAIL = "e@mail"
private const val SCHOOLID = "schoolId"
private const val DISPLAYNAME = "displayName"
private const val NAME = "firstName lastName"
private const val SHORTNAME = "f. lastName"

object UserSpec : Spek({
    describe("A user") {
        val user by memoized {
            User().apply {
                id = ID
                firstName = FIRSTNAME
                lastName = LASTNAME
                email = EMAIL
                schoolId = SCHOOLID
                displayName = DISPLAYNAME
            }
    }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(ID, user.id)
                assertEquals(FIRSTNAME, user.firstName)
                assertEquals(LASTNAME, user.lastName)
                assertEquals(EMAIL, user.email)
                assertEquals(SCHOOLID, user.schoolId)
                assertEquals(DISPLAYNAME, user.displayName)
            }

            it("name should include firstName and lastName"){
                assertEquals(NAME, user.name)
            }

            it("shortName should include initial and lastName"){
                assertEquals(SHORTNAME, user.shortName)
            }
        }
    }
})
