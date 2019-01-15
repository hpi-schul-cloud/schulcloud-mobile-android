package org.schulcloud.mobile.models.event

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private const val TYPE = "type"
private const val ID = "id"
private val ATTRIBUTES = IncludedAttributes()

object IncludedSpec : Spek({
    describe("An included") {
        val included by memoized {
            Included().apply {
                type = TYPE
                id = ID
                attributes = ATTRIBUTES
            }
        }

        describe("Property access") {
            it("should return the assigned value") {
                assertEquals(TYPE, included.type)
                assertEquals(ID, included.id)
                assertEquals(ATTRIBUTES, included.attributes)
            }
        }
    }
})