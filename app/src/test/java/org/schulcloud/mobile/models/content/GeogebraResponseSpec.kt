package org.schulcloud.mobile.models.content

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private val RESPONSES = GeogebraResponse.Responses()
private val RESPONSE = GeogebraResponse.Responses.Response()
private val ITEM = GeogebraResponse.Responses.Response.Item()
private const val PREVIEW_URL = "previewUrl"

object GeogebraResponseSpec : Spek({
    describe("A geogebraResponse") {
        val geogebraResponse by memoized {
            GeogebraResponse().apply {
                responses = RESPONSES.apply {
                    response = RESPONSE.apply {
                        item = ITEM.apply {
                            previewUrl = PREVIEW_URL
                        }
                    }
                }
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(RESPONSES, geogebraResponse.responses)
                assertEquals(RESPONSE, geogebraResponse.responses?.response)
                assertEquals(ITEM, geogebraResponse.responses?.response?.item)
                assertEquals(PREVIEW_URL, geogebraResponse.responses?.response?.item?.previewUrl)
            }
        }
    }
})
