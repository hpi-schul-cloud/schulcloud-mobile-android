package org.schulcloud.mobile.models.file

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private const val KEY = "key"
private const val NAME = "name"
private const val PATH = "path"
private const val ID_KEY_NOT_NULL = "key"
private const val ID_KEY_NULL = ""

object DirectorySpec : Spek({
    describe("A directory") {
        val directory by memoized {
            Directory().apply {
                key = KEY
                name = NAME
                path = PATH
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(KEY, directory.key)
                assertEquals(NAME, directory.name)
                assertEquals(PATH, directory.path)
            }
        }

        describe("setting key null") {
            beforeEach {
                directory.key = null
            }
            it("id should be empty") {
                assertEquals(ID_KEY_NULL, directory.id)
            }
        }

        describe("setting key not null") {
            beforeEach {
                directory.key = KEY
            }
            it("id should not be empty") {
                assertEquals(ID_KEY_NOT_NULL, directory.id)
            }
        }
    }
})
