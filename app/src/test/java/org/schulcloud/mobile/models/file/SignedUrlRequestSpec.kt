package org.schulcloud.mobile.models.file

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private const val ACTION = "action"
private const val PATH = "path"
private const val FILETYPE = "fileType"

object SignedUrlRequestSpec : Spek({
    describe("A signedUrlRequest") {
        val signedUrlRequest by memoized {
            SignedUrlRequest().apply {
                action = ACTION
                path = PATH
                fileType = FILETYPE
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(ACTION, signedUrlRequest.action)
                assertEquals(PATH, signedUrlRequest.path)
                assertEquals(FILETYPE, signedUrlRequest.fileType)
            }
        }
    }
})
