package org.schulcloud.mobile.models.file

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SignedUrlResponseTest {
    private companion object {
        const val URL = "url"
        const val CONTENTTYPE = "contentType"
        const val METAPATH = "metaPath"
        const val METANAME = "metaName"
        const val METAFLATNAME = "metaFlatName"
        const val METATHUMBNAIL = "metaThumbnail"
    }

    private val newSignedUrlResponse: SignedUrlResponse
        get() = SignedUrlResponse().apply {
            url = URL
            header = SignedUrlResponse.SignedUrlResponseHeader().apply {
                contentType = CONTENTTYPE
                metaPath = METAPATH
                metaName = METANAME
                metaFlatName = METAFLATNAME
                metaThumbnail = METATHUMBNAIL
            }
        }
    private lateinit var signedUrlResponse: SignedUrlResponse

    @Before
    fun setUp() {
        signedUrlResponse = newSignedUrlResponse
    }

    @Test
    fun testGetProperties() {
        assertEquals(URL, signedUrlResponse.url)
        assertEquals(CONTENTTYPE, signedUrlResponse.header?.contentType)
        assertEquals(METAPATH, signedUrlResponse.header?.metaPath)
        assertEquals(METANAME, signedUrlResponse.header?.metaName)
        assertEquals(METAFLATNAME, signedUrlResponse.header?.metaFlatName)
        assertEquals(METATHUMBNAIL, signedUrlResponse.header?.metaThumbnail)
    }
}
