package org.schulcloud.mobile.models.content

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private val responses = GeogebraResponse.Responses()
private val response = GeogebraResponse.Responses.Response()
private val item = GeogebraResponse.Responses.Response.Item()
private const val PREVIEW_URL = "previewUrl"

object GeogebraResponseSpec : Spek({
    describe("A geogebraResponse") {
        val geogebraResponse by memoized {
            GeogebraResponse().also {
                it.responses = responses.also {
                    it.response = response.also {
                        it.item = item.also {
                            it.previewUrl = PREVIEW_URL
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
                assertEquals(PREVIEW_URL, geogebraResponse.responses?.response?.item?.previewUrl)
            }
        }
    }
})
