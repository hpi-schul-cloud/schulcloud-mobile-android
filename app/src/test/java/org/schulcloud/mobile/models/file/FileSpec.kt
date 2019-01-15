package org.schulcloud.mobile.models.file

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private const val KEY = "key"
private const val NAME = "name"
private const val PATH = "path"
private const val SIZE = 100L
private const val TYPE = "type"
private const val THUMBNAIL = "thumbnail"
private const val FLATFILENAME = "flatName"
private const val ID = "key"

object FileSpec : Spek({
    describe("A file") {
        val file by memoized {
            File().apply {
                key = KEY
                name = NAME
                path = PATH
                size = SIZE
                type = TYPE
                thumbnail = THUMBNAIL
                flatFileName = FLATFILENAME

            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(KEY, file.key)
                assertEquals(NAME, file.name)
                assertEquals(PATH, file.path)
                assertEquals(SIZE, file.size)
                assertEquals(TYPE, file.type)
                assertEquals(THUMBNAIL, file.thumbnail)
                assertEquals(FLATFILENAME, file.flatFileName)
            }

            it("id should equal key") {
                assertEquals(ID, file.id)
            }
        }
    }
})
