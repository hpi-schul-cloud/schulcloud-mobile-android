package org.schulcloud.mobile.data.model.requestBodies;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CallbackRequestTest {
    private static final String TYPE = "ERNST";
    private static final String NOTIFICATIONID = "1234567890";

    private CallbackRequest callbackRequest;

    @Before
    public void setUp() {
        callbackRequest = createNewCallbackRequest();
    }

    @Test
    public void testGetProperties() {
        assertEquals(callbackRequest.notificationId, NOTIFICATIONID);
        assertEquals(callbackRequest.type, TYPE);
    }

    public static CallbackRequest createNewCallbackRequest() {
        CallbackRequest callbackRequest = new CallbackRequest(NOTIFICATIONID, TYPE);

        return callbackRequest;
    }

}