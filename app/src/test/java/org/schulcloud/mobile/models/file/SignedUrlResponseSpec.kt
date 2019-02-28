package org.schulcloud.mobile.models.file

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object SignedUrlResponseSpec : Spek({
    val url = "url"
    val contentType = "contentType"
    val metaPath = "metaPath"
    val metaName = "metaName"
    val metaFlatName = "metaFlatName"
    val metaThumbnail = "metaThumbnail"

    describe("A SignedUrlResponse") {
        val signedUrlResponse by memoized {
            SignedUrlResponse().apply {
                this.url = url
                header = SignedUrlResponse.SignedUrlResponseHeader().apply {
                    this.contentType = contentType
                    this.metaPath = metaPath
                    this.metaName = metaName
                    this.metaFlatName = metaFlatName
                    this.metaThumbnail = metaThumbnail
                }
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(url, signedUrlResponse.url)
                assertEquals(contentType, signedUrlResponse.header?.contentType)
                assertEquals(metaPath, signedUrlResponse.header?.metaPath)
                assertEquals(metaName, signedUrlResponse.header?.metaName)
                assertEquals(metaFlatName, signedUrlResponse.header?.metaFlatName)
                assertEquals(metaThumbnail, signedUrlResponse.header?.metaThumbnail)
            }
        }
    }
})
