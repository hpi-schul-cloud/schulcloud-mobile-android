package org.schulcloud.mobile.models

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object AccessTokenSpec : Spek({
    val accessTokenText = "accessToken"

    describe("An accessToken") {
        val accessToken by memoized {
        AccessToken().apply {
            this.accessToken = accessTokenText
        }
    }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(accessTokenText, accessToken.accessToken)
            }
        }
    }
})
