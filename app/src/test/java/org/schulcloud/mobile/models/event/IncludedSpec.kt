package org.schulcloud.mobile.models.event

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private const val TYPE = "type"
private const val ID = "id"
private val attributes = IncludedAttributes()

object IncludedSpec : Spek({
    describe("An included") {
        val included by memoized {
            Included().also {
                it.type = TYPE
                it.id = ID
                it.attributes = attributes
            }
        }

        describe("Property access") {
            it("should return the assigned value") {
                assertEquals(TYPE, included.type)
                assertEquals(ID, included.id)
                assertEquals(attributes, included.attributes)
            }
        }
    }
})
