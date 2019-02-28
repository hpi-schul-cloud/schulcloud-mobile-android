package org.schulcloud.mobile.models.event

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object IncludedSpec : Spek({
    val type = "type"
    val id = "id"
    val attributes = IncludedAttributes()

    describe("An included") {
        val included by memoized {
            Included().apply {
                this.type = type
                this.id = id
                this.attributes = attributes
            }
        }

        describe("Property access") {
            it("should return the assigned value") {
                assertEquals(type, included.type)
                assertEquals(id, included.id)
                assertEquals(attributes, included.attributes)
            }
        }
    }
})
