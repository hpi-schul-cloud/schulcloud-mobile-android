package org.schulcloud.mobile.models.homework.submission

import org.junit.Assert.*
import io.realm.RealmList
import org.junit.Before
import org.junit.Test


class SubmissionTest {
    private companion object {
        const val ID = "id"
        const val HOMEWORKID = "homeworkId"
        const val STUDENTID = "studentId"
        const val COMMENT = "comment"
        const val CREATEDAT = "createdAt"
        const val GRADE = 15
        const val GRADECOMMENT = "gradeComment"
        val COMMENTS = RealmList<Comment>()
    }

    private val newSubmission: Submission
        get() = Submission().apply {
            id = ID
            homeworkId = HOMEWORKID
            studentId = STUDENTID
            comment = COMMENT
            createdAt = CREATEDAT
            grade = GRADE
            gradeComment = GRADECOMMENT
            comments = COMMENTS
        }
    private lateinit var submission: Submission

    @Before
    fun setUp() {
        submission = newSubmission
    }

    @Test
    fun testGetProperties() {
        assertEquals(ID, submission.id)
        assertEquals(HOMEWORKID, submission.homeworkId)
        assertEquals(STUDENTID, submission.studentId)
        assertEquals(COMMENT, submission.comment)
        assertEquals(CREATEDAT, submission.createdAt)
        assertEquals(GRADE, submission.grade)
        assertEquals(GRADECOMMENT, submission.gradeComment)
        assertEquals(COMMENTS, submission.comments)
    }
}
