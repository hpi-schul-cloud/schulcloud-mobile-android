package org.schulcloud.mobile.models.content

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private const val URL = "url"
private const val CLIENT = "client"
private const val TITLE = "title"
private const val DESCRIPTION = "description"

object ResourceSpec : Spek({
    describe("A resource") {
        val resource by memoized {
            Resource().apply {
                url = URL
                client = CLIENT
                title = TITLE
                description = DESCRIPTION
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(URL, resource.url)
                assertEquals(CLIENT, resource.client)
                assertEquals(TITLE, resource.title)
                assertEquals(DESCRIPTION, resource.description)
            }
        }
    }
})
