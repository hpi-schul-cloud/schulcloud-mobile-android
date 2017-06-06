package org.schulcloud.mobile.data.model;

import org.junit.Before;
import org.junit.Test;

import io.realm.RealmList;

import static junit.framework.Assert.assertEquals;

public class SubmissionTest {
    private static final String ID = "ID";
    private static final String SCHOOLDID = "schoolId";
    private static final String STUDENTID = "studentId";
    private static final String COMMENT = "comment";
    private static final RealmList<Comment> COMMENTS = new RealmList<>();
    private static final Integer GRADE = 99;
    private static final String HOMEWORKID = "homeworkId";
    private static final String GRADECOMMENT = "cool";
    private static final String CREATEDAT = "invisible";

    private Submission submission;

    @Before
    public void setUp() throws Exception {
        submission = createNewSubmission();
    }

    @Test
    public void testGetProperties() {
        assertEquals(submission._id, ID);
        assertEquals(submission.schoolId, SCHOOLDID);
        assertEquals(submission.studentId, STUDENTID);
        assertEquals(submission.comment, COMMENT);
        assertEquals(submission.gradeComment, GRADECOMMENT);
        assertEquals(submission.createdAt, CREATEDAT);
        assertEquals(submission.comments, COMMENTS);
        assertEquals(submission.homeworkId, HOMEWORKID);
        assertEquals(submission.grade, GRADE);
    }

    public static Submission createNewSubmission() {
        Submission s = new Submission();
        s._id = ID;
        s.schoolId = SCHOOLDID;
        s.studentId = STUDENTID;
        s.comment = COMMENT;
        s.comments = COMMENTS;
        s.gradeComment = GRADECOMMENT;
        s.grade = GRADE;
        s.homeworkId = HOMEWORKID;
        s.createdAt = CREATEDAT;

        return s;
    }
}