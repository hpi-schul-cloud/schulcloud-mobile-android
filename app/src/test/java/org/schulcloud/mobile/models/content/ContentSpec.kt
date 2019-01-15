package org.schulcloud.mobile.models.content

import io.realm.RealmList
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private const val TEXT = "text"
private val RESOURCES = RealmList<Resource>()
private const val URL = "url"
private const val MATERIALID = "materialId"
private const val TITLE = "title"
private const val DESCRIPTION = "description"

object ContentSpec : Spek({
    describe("An ") {
        val content by memoized {
        Content().apply {
            text = TEXT
            resources = RESOURCES
            url = URL
            materialId = MATERIALID
            title = TITLE
            description = DESCRIPTION
        }
    }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(TEXT, content.text)
                assertEquals(RESOURCES, content.resources)
                assertEquals(URL, content.url)
                assertEquals(MATERIALID, content.materialId)
                assertEquals(TITLE, content.title)
                assertEquals(DESCRIPTION, content.description)
            }
        }
    }
})
