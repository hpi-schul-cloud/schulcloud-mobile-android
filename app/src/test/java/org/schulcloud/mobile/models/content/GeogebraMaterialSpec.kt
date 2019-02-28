package org.schulcloud.mobile.models.content

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object GeogebraMaterialSpec : Spek({
    val id = "id"
    val previewUrl = "previewUrl"

    describe("A geogebraMaterial") {
        val geogebraMaterial by memoized {
            GeogebraMaterial().apply {
                this.id = id
                this.previewUrl = previewUrl
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(id, geogebraMaterial.id)
                assertEquals(previewUrl, geogebraMaterial.previewUrl)
            }
        }
    }
})
