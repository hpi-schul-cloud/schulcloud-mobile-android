package org.schulcloud.mobile.models.file

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FileTest {
    private companion object {
        const val KEY = "key"
        const val NAME = "name"
        const val PATH = "path"
        const val SIZE = 100L
        const val TYPE = "type"
        const val THUMBNAIL = "thumbnail"
        const val FLATFILENAME = "flatName"
        const val ID = "key"
    }

    private val newFile: File
        get() = File().apply {
            key = KEY
            name = NAME
            path = PATH
            size = SIZE
            type = TYPE
            thumbnail = THUMBNAIL
            flatFileName = FLATFILENAME

        }
    private lateinit var file: File

    @Before
    fun setUp() {
        file = newFile
    }

    @Test
    fun testGetProperties() {
        assertEquals(KEY, file.key)
        assertEquals(NAME, file.name)
        assertEquals(PATH, file.path)
        assertEquals(SIZE, file.size)
        assertEquals(TYPE, file.type)
        assertEquals(THUMBNAIL, file.thumbnail)
        assertEquals(FLATFILENAME, file.flatFileName)
    }

    @Test
    fun testIdEqualsKey() {
        assertEquals(ID, file.id)
    }
}
