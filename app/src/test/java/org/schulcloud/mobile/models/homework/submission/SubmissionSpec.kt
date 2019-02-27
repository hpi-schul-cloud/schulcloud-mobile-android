package org.schulcloud.mobile.models.homework.submission

import io.realm.RealmList
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private const val ID = "id"
private const val HOMEWORKID = "homeworkId"
private const val STUDENTID = "studentId"
private const val COMMENT = "comment"
private const val CREATEDAT = "createdAt"
private const val GRADE = 15
private const val GRADECOMMENT = "gradeComment"
private val comments = RealmList<Comment>()

object SubmissionSpec : Spek({
    describe("A submission") {
        val submission by memoized {
            Submission().also {
                it.id = ID
                it.homeworkId = HOMEWORKID
                it.studentId = STUDENTID
                it.comment = COMMENT
                it.createdAt = CREATEDAT
                it.grade = GRADE
                it.gradeComment = GRADECOMMENT
                it.comments = comments
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(ID, submission.id)
                assertEquals(HOMEWORKID, submission.homeworkId)
                assertEquals(STUDENTID, submission.studentId)
                assertEquals(COMMENT, submission.comment)
                assertEquals(CREATEDAT, submission.createdAt)
                assertEquals(GRADE, submission.grade)
                assertEquals(GRADECOMMENT, submission.gradeComment)
                assertEquals(comments, submission.comments)
            }
        }
    }
})
