package org.schulcloud.mobile.models.content

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private const val ID = "id"
private const val PREVIEW_URL = "previewUrl"

object GeogebraMaterialSpec : Spek({
    describe("A geogebraMaterial") {
        val geogebraMaterial by memoized {
            GeogebraMaterial().apply {
                id = ID
                previewUrl = PREVIEW_URL
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(ID, geogebraMaterial.id)
                assertEquals(PREVIEW_URL, geogebraMaterial.previewUrl)
            }
        }
    }
})
