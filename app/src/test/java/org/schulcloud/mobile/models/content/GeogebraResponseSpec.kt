package org.schulcloud.mobile.models.content

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object GeogebraResponseSpec : Spek({
    val responses = GeogebraResponse.Responses()
    val response = GeogebraResponse.Responses.Response()
    val item = GeogebraResponse.Responses.Response.Item()
    val previewUrl = "previewUrl"

    describe("A geogebraResponse") {
        val geogebraResponse by memoized {
            GeogebraResponse().apply {
                this.responses = responses.apply {
                    this.response = response.apply {
                        this.item = item.apply{
                            this.previewUrl = previewUrl
                        }
                    }
                }
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(responses, geogebraResponse.responses)
                assertEquals(response, geogebraResponse.responses?.response)
                assertEquals(item, geogebraResponse.responses?.response?.item)
                assertEquals(previewUrl, geogebraResponse.responses?.response?.item?.previewUrl)
            }
        }
    }
})
