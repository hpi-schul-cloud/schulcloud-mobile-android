package org.schulcloud.mobile.models.homework.submission

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CommentTest {
    private companion object {
        const val ID = "id"
        const val SUBMISSIONID = "submissionId"
        const val AUTHOR = "author"
        const val COMMENT = "comment"
        const val CREATEDAT = "createdAt"
    }

    private val newComment: Comment
        get() = Comment().apply {
            id = ID
            submissionId = SUBMISSIONID
            author = AUTHOR
            comment = COMMENT
            createdAt = CREATEDAT
        }
    private lateinit var comment: Comment

    @Before
    fun setUp() {
        comment = newComment
    }

    @Test
    fun testGetProperties() {
        assertEquals(ID, comment.id)
        assertEquals(SUBMISSIONID, comment.submissionId)
        assertEquals(AUTHOR, comment.author)
        assertEquals(COMMENT, comment.comment)
        assertEquals(CREATEDAT, comment.createdAt)
    }
}
