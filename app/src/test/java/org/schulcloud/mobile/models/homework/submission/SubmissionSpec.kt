package org.schulcloud.mobile.models.homework.submission

import io.realm.RealmList
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object SubmissionSpec : Spek({
    val id = "id"
    val homeworkId = "homeworkId"
    val studentId = "studentId"
    val comment = "comment"
    val createdAt = "createdAt"
    val grade = 15
    val gradeComment = "gradeComment"
    val comments = RealmList<Comment>()

    describe("A submission") {
        val submission by memoized {
            Submission().apply {
                this.id = id
                this.homeworkId = homeworkId
                this.studentId = studentId
                this.comment = comment
                this.createdAt = createdAt
                this.grade = grade
                this.gradeComment = gradeComment
                this.comments = comments
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(id, submission.id)
                assertEquals(homeworkId, submission.homeworkId)
                assertEquals(studentId, submission.studentId)
                assertEquals(comment, submission.comment)
                assertEquals(createdAt, submission.createdAt)
                assertEquals(grade, submission.grade)
                assertEquals(gradeComment, submission.gradeComment)
                assertEquals(comments, submission.comments)
            }
        }
    }
})
