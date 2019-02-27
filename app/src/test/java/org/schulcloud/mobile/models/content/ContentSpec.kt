package org.schulcloud.mobile.models.content

import io.realm.RealmList
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private const val TEXT = "text"
private val resources = RealmList<Resource>()
private const val URL = "url"
private const val MATERIALID = "materialId"
private const val TITLE = "title"
private const val DESCRIPTION = "description"

object ContentSpec : Spek({
    describe("A  content") {
        val content by memoized {
        Content().also {
            it.text = TEXT
            it.resources = resources
            it.url = URL
            it.materialId = MATERIALID
            it.title = TITLE
            it.description = DESCRIPTION
        }
    }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(TEXT, content.text)
                assertEquals(resources, content.resources)
                assertEquals(URL, content.url)
                assertEquals(MATERIALID, content.materialId)
                assertEquals(TITLE, content.title)
                assertEquals(DESCRIPTION, content.description)
            }
        }
    }
})
