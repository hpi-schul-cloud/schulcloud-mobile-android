package org.schulcloud.mobile.data.model.requestBodies;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class SignedUrlRequestTest {

    private static final String ACTION = "ACTION";
    private static final String PATH = "PATH";
    private static final String FILE_TYPE = "FILE_TYPE";

    private SignedUrlRequest signedUrlRequest;

    @Before
    public void setUp() throws Exception {
        signedUrlRequest = newSignedUrlRequest();
    }

    @Test
    public void testForConstructor() {
        assertEquals(signedUrlRequest.action, ACTION);
        assertEquals(signedUrlRequest.path, PATH);
        assertEquals(signedUrlRequest.fileType, FILE_TYPE);
    }

    public static SignedUrlRequest newSignedUrlRequest() {
        return new SignedUrlRequest(ACTION, PATH, FILE_TYPE);
    }


}