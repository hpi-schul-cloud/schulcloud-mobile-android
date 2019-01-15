package org.schulcloud.mobile.models.event

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private const val FREQ = "freq"
private const val UNTIL = "until"
private const val WEEKDAY = "weekday"
private const val INVALID_WEEKDAY = "invWeekday"
private const val VALID_WEEKDAY = "SU"

object IncludedAttributesSpec : Spek({
    describe("Included attributes") {
        val includedAttributes by memoized {
            IncludedAttributes().apply {
                freq = FREQ
                until = UNTIL
                weekday = WEEKDAY
            }

        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(FREQ, includedAttributes.freq)
                assertEquals(UNTIL, includedAttributes.until)
                assertEquals(WEEKDAY, includedAttributes.weekday)
            }
        }

        describe("setting valid weekday") {
            beforeEach {
                includedAttributes.weekday = VALID_WEEKDAY
            }

            it("weekdayNumber should not be null") {
                assertNotNull(includedAttributes.weekdayNumber)
            }
        }

        describe("setting invalid weekday") {
            beforeEach {
                includedAttributes.weekday = INVALID_WEEKDAY
            }

            it("weekdayNumber should be null") {
                assertNull(includedAttributes.weekdayNumber)
            }
        }
    }
})
