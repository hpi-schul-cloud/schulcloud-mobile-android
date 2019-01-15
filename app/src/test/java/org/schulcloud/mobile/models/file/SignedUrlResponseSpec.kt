package org.schulcloud.mobile.models.file

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private const val URL = "url"
private const val CONTENTTYPE = "contentType"
private const val METAPATH = "metaPath"
private const val METANAME = "metaName"
private const val METAFLATNAME = "metaFlatName"
private const val METATHUMBNAIL = "metaThumbnail"

object SignedUrlResponseSpec : Spek({
    describe("A SignedUrlResponse") {
        val signedUrlResponse by memoized {
            SignedUrlResponse().apply {
                url = URL
                header = SignedUrlResponse.SignedUrlResponseHeader().apply {
                    contentType = CONTENTTYPE
                    metaPath = METAPATH
                    metaName = METANAME
                    metaFlatName = METAFLATNAME
                    metaThumbnail = METATHUMBNAIL
                }
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(URL, signedUrlResponse.url)
                assertEquals(CONTENTTYPE, signedUrlResponse.header?.contentType)
                assertEquals(METAPATH, signedUrlResponse.header?.metaPath)
                assertEquals(METANAME, signedUrlResponse.header?.metaName)
                assertEquals(METAFLATNAME, signedUrlResponse.header?.metaFlatName)
                assertEquals(METATHUMBNAIL, signedUrlResponse.header?.metaThumbnail)
            }
        }
    }
})
