package org.schulcloud.mobile.data.model.responseBodies;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class SignedUrlResponseTest {

    private static final String URL = "URL";
    private static final String CONTENT_TYPE = "CONTENT_TYPE";
    private static final String NAME = "NAME";
    private static final String PATH = "PATH";
    private static final String THUMBNAIL = "THUMBNAIL";

    private SignedUrlResponse signedUrlResponse;

    @Before
    public void setUp() throws Exception {
        signedUrlResponse = newSignedUrlResponse();
    }

    @Test
    public void testForConstructor() {
        assertEquals(signedUrlResponse.url, URL);
        assertEquals(signedUrlResponse.header.getContentType(), CONTENT_TYPE);
        assertEquals(signedUrlResponse.header.getMetaName(), NAME);
        assertEquals(signedUrlResponse.header.getMetaPath(), PATH);
        assertEquals(signedUrlResponse.header.getMetaThumbnail(), THUMBNAIL);
    }

    public static SignedUrlResponse newSignedUrlResponse() {
        SignedUrlResponse signedUrlResponse = new SignedUrlResponse();
        signedUrlResponse.url = URL;
        SignedUrlResponse.SignedUrlResponseHeader header = new SignedUrlResponse.SignedUrlResponseHeader();
        header.setContentType(CONTENT_TYPE);
        header.setMetaName(NAME);
        header.setMetaPath(PATH);
        header.setMetaThumbnail(THUMBNAIL);

        signedUrlResponse.header = header;
        return signedUrlResponse;
    }

}