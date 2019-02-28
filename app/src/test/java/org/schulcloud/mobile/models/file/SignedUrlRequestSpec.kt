package org.schulcloud.mobile.models.file

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object SignedUrlRequestSpec : Spek({
    val action = "action"
    val path = "path"
    val fileType = "fileType"

    describe("A signedUrlRequest") {
        val signedUrlRequest by memoized {
            SignedUrlRequest().apply {
                this.action = action
                this.path = path
                this.fileType = fileType
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(action, signedUrlRequest.action)
                assertEquals(path, signedUrlRequest.path)
                assertEquals(fileType, signedUrlRequest.fileType)
            }
        }
    }
})
