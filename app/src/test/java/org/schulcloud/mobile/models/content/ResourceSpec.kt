package org.schulcloud.mobile.models.content

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object ResourceSpec : Spek({
    val url = "url"
    val client = "client"
    val title = "title"
    val description = "description"

    describe("A resource") {
        val resource by memoized {
            Resource().apply {
                this.url = url
                this.client = client
                this.title = title
                this.description = description
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(url, resource.url)
                assertEquals(client, resource.client)
                assertEquals(title, resource.title)
                assertEquals(description, resource.description)
            }
        }
    }
})
