package org.schulcloud.mobile.models.event

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

object IncludedAttributesSpec : Spek({
    val freq = "freq"
    val until = "until"
    val weekday = "weekday"
    val invalidWeekday = "invWeekday"
    val validWeekdays = listOf("SU", "MO", "TU", "WE", "TH", "FR", "SA")

    describe("Included attributes") {
        val includedAttributes by memoized {
            IncludedAttributes().apply {
                this.freq = freq
                this.until = until
                this.weekday = weekday
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(freq, includedAttributes.freq)
                assertEquals(until, includedAttributes.until)
                assertEquals(weekday, includedAttributes.weekday)
            }
        }


        describe("setting valid weekday") {
            validWeekdays.forEach { validWeekday ->
                beforeEach {
                    includedAttributes.weekday = validWeekday
                }

                it("weekdayNumber should not be null") {
                    assertNotNull(includedAttributes.weekdayNumber)
                }
            }
        }


        describe("setting invalid weekday") {
            beforeEach {
                includedAttributes.weekday = invalidWeekday
            }

            it("weekdayNumber should be null") {
                assertNull(includedAttributes.weekdayNumber)
            }
        }
    }
})
