package org.schulcloud.mobile.models

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object CredentialsSpec : Spek({
    val username = "username"
    val password = "password"


    describe("Credentials") {
        val credentials by memoized {
        Credentials(username, password)
    }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(username, credentials.username)
                assertEquals(password, credentials.password)
            }
        }
    }
})
