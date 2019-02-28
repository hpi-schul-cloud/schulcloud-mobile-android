package org.schulcloud.mobile.models.news

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object NewsSpec : Spek({
    val id = "id"
    val schoolId = "schoolId"
    val title = "title"
    val content = "content"
    val createdAt = "createdAt"

    describe("News") {
        val news by memoized {
            News().apply {
                this.id = id
                this.schoolId = schoolId
                this.title = title
                this.content = content
                this.createdAt = createdAt
            }
    }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(id, news.id)
                assertEquals(schoolId, news.schoolId)
                assertEquals(title, news.title)
                assertEquals(content, news.content)
                assertEquals(createdAt, news.createdAt)
            }
        }
    }
})
