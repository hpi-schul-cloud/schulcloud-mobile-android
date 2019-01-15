package org.schulcloud.mobile.models.news

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private const val ID = "id"
private const val SCHOOLID = "schoolId"
private const val TITLE = "title"
private const val CONTENT = "content"
private const val CREATEDAT = "createdAt"

object NewsSpec : Spek({
    describe("News") {
        val news by memoized {
            News().apply {
                id = ID
                schoolId = SCHOOLID
                title = TITLE
                content = CONTENT
                createdAt = CREATEDAT
            }
    }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(ID, news.id)
                assertEquals(SCHOOLID, news.schoolId)
                assertEquals(TITLE, news.title)
                assertEquals(CONTENT, news.content)
                assertEquals(CREATEDAT, news.createdAt)
            }
        }
    }
})
