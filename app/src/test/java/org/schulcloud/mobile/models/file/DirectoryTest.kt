/*
package org.schulcloud.mobile.models.file

import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class DirectoryTest {
    private companion object {
        const val KEY = "key"
        const val NAME = "name"
        const val PATH = "path"
        const val ID_KEY_NOT_NULL = "key"
        const val ID_KEY_NULL = ""
    }

    private val newDirectory : Directory
        get() = Directory().apply {
            key = KEY
            name = NAME
            path = PATH
        }
    private lateinit var directory: Directory

    @Before
    fun setUp() {
        directory = newDirectory
    }

    @Test
    fun testGetProperties(){
        assertEquals(KEY, directory.key)
        assertEquals(NAME, directory.name)
        assertEquals(PATH, directory.path)
    }

    @Test
    fun testIdEqualsKeyWhenKeyNotNull(){
        directory.key = KEY
        assertEquals(ID_KEY_NOT_NULL, directory.id)
    }

    @Test
    fun testIdEmptyWhenKeyNull(){
        directory.key = null
        assertEquals(ID_KEY_NULL, directory.id)
    }
}
*/
