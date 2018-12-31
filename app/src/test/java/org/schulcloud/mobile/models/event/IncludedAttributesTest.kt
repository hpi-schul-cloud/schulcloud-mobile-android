package org.schulcloud.mobile.models.event

import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class IncludedAttributesTest {
    private companion object {
        const val FREQ = "freq"
        const val UNTIL = "until"
        const val WEEKDAY = "weekday"
        const val INVALID_WEEKDAY = "invWeekday"
        const val VALID_WEEKDAY = "SU"
    }

    private val newIncludedAttributes: IncludedAttributes
        get() = IncludedAttributes().apply {
            freq = FREQ
            until = UNTIL
            weekday = WEEKDAY
        }
    private lateinit var includedAttributes: IncludedAttributes

    @Before
    fun setUp() {
        includedAttributes = newIncludedAttributes
    }

    @Test
    fun testGetProperties() {
        assertEquals(FREQ, includedAttributes.freq)
        assertEquals(UNTIL, includedAttributes.until)
        assertEquals(WEEKDAY, includedAttributes.weekday)
    }

    @Test
    fun testWeekdayNumberNotNullWhenWeekdayValid() {
        includedAttributes.weekday = VALID_WEEKDAY
        assertNotNull(includedAttributes.weekdayNumber)
    }

    @Test
    fun testWeekdayNumberNullWhenWeekdayInvalid() {
        includedAttributes.weekday = INVALID_WEEKDAY
        assertNull(includedAttributes.weekdayNumber)
    }
}
