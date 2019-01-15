package org.schulcloud.mobile.models.file

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private val FILES = emptyList<File>()
private val DIRECTORIES = emptyList<Directory>()

object DirectoryResponseSpec : Spek({
    describe("A directoryResponse") {
        val directoryResponse by memoized {
            DirectoryResponse().apply {
                files = FILES
                directories = DIRECTORIES
            }
        }

        describe("Property access") {
            it("should return the assigned value") {
                assertEquals(FILES, directoryResponse.files)
                assertEquals(DIRECTORIES, directoryResponse.directories)
            }
        }
    }
})
