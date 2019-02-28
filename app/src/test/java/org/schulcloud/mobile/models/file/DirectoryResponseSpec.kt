package org.schulcloud.mobile.models.file

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object DirectoryResponseSpec : Spek({
    val files = emptyList<File>()
    val directories = emptyList<Directory>()

    describe("A directoryResponse") {
        val directoryResponse by memoized {
            DirectoryResponse().apply {
                this.files = files
                this.directories = directories
            }
        }

        describe("Property access") {
            it("should return the assigned value") {
                assertEquals(files, directoryResponse.files)
                assertEquals(directories, directoryResponse.directories)
            }
        }
    }
})
