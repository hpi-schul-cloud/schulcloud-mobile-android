package org.schulcloud.mobile.models.file

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object DirectorySpec : Spek({
    val key = "key"
    val name = "name"
    val path = "path"
    val idKey = "key"
    val idEmpty = ""

    describe("A directory") {
        val directory by memoized {
            Directory().apply {
                this.key = key
                this.name = name
                this.path = path
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(key, directory.key)
                assertEquals(name, directory.name)
                assertEquals(path, directory.path)
            }
        }

        describe("setting key null") {
            beforeEach {
                directory.key = null
            }
            it("id should be empty") {
                assertEquals(idEmpty, directory.id)
            }
        }

        describe("setting key not null") {
            beforeEach {
                directory.key = key
            }
            it("id should equal key") {
                assertEquals(idKey, directory.id)
            }
        }
    }
})
