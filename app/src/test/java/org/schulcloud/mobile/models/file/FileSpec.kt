package org.schulcloud.mobile.models.file

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object FileSpec : Spek({
    val key = "key"
    val name = "name"
    val path = "path"
    val size = 100L
    val type = "type"
    val thumbnail = "thumbnail"
    val flatFileName = "flatName"
    val idKey = "key"

    describe("A file") {
        val file by memoized {
            File().apply {
                this.key = key
                this.name = name
                this.path = path
                this.size = size
                this.type = type
                this.thumbnail = thumbnail
                this.flatFileName = flatFileName
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(key, file.key)
                assertEquals(name, file.name)
                assertEquals(path, file.path)
                assertEquals(size, file.size)
                assertEquals(type, file.type)
                assertEquals(thumbnail, file.thumbnail)
                assertEquals(flatFileName, file.flatFileName)
            }

            it("id should equal key") {
                assertEquals(idKey, file.id)
            }
        }
    }
})
