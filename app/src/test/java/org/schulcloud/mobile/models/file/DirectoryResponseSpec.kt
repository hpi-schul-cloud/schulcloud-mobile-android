package org.schulcloud.mobile.models.file

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private val files = emptyList<File>()
private val directories = emptyList<Directory>()

object DirectoryResponseSpec : Spek({
    describe("A directoryResponse") {
        val directoryResponse by memoized {
            DirectoryResponse().also {
                it.files = files
                it.directories = directories
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
