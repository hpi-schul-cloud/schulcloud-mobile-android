package org.schulcloud.mobile.models.homework.submission

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private const val ID = "id"
private const val SUBMISSIONID = "submissionId"
private const val AUTHOR = "author"
private const val COMMENT = "comment"
private const val CREATEDAT = "createdAt"

object CommentSpec : Spek({
    describe("A comment") {
        val comment by memoized {
            Comment().apply {
                id = ID
                submissionId = SUBMISSIONID
                author = AUTHOR
                comment = COMMENT
                createdAt = CREATEDAT
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(ID, comment.id)
                assertEquals(SUBMISSIONID, comment.submissionId)
                assertEquals(AUTHOR, comment.author)
                assertEquals(COMMENT, comment.comment)
                assertEquals(CREATEDAT, comment.createdAt)
            }
        }
    }
})
