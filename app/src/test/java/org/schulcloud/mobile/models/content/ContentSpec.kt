package org.schulcloud.mobile.models.content

import io.realm.RealmList
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object ContentSpec : Spek({
    val text = "text"
    val resources = RealmList<Resource>()
    val url = "url"
    val materialId = "materialId"
    val title = "title"
    val description = "description"

    describe("A  content") {
        val content by memoized {
        Content().apply {
            this.text = text
            this.resources = resources
            this.url = url
            this.materialId = materialId
            this.title = title
            this.description = description
        }
    }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(text, content.text)
                assertEquals(resources, content.resources)
                assertEquals(url, content.url)
                assertEquals(materialId, content.materialId)
                assertEquals(title, content.title)
                assertEquals(description, content.description)
            }
        }
    }
})
