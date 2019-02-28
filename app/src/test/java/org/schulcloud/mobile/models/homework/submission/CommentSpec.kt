package org.schulcloud.mobile.models.homework.submission

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object CommentSpec : Spek({
    val id = "id"
    val submissionId = "submissionId"
    val author = "author"
    val commentText = "comment"
    val createdAt = "createdAt"
    
    describe("A comment") {
        val comment by memoized {
            Comment().apply {
                this.id = id
                this.submissionId = submissionId
                this.author = author
                comment = commentText
                this.createdAt = createdAt
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(id, comment.id)
                assertEquals(submissionId, comment.submissionId)
                assertEquals(author, comment.author)
                assertEquals(commentText, comment.comment)
                assertEquals(createdAt, comment.createdAt)
            }
        }
    }
})
