package org.schulcloud.mobile.data.model;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class CommentTest {
    private static final String ID = "ID";
    private static final String SUBMISSIONID = "1234";
    private static final String AUTHOR = "author";
    private static final String COMMENT = "comment";
    private static final String CREATEDAT = "invisible";

    private Comment comment;

    @Before
    public void setUp() throws Exception {
        comment = createNewComment();
    }

    @Test
    public void testGetProperties() {
        assertEquals(comment._id, ID);
        assertEquals(comment.author, AUTHOR);
        assertEquals(comment.comment, COMMENT);
        assertEquals(comment.submissionId, SUBMISSIONID);
        assertEquals(comment.createdAt, CREATEDAT);
    }

    public static Comment createNewComment() {
        Comment c = new Comment();
        c._id = ID;
        c.submissionId = SUBMISSIONID;
        c.author = AUTHOR;
        c.comment = COMMENT;
        c.createdAt = CREATEDAT;

        return c;
    }
}