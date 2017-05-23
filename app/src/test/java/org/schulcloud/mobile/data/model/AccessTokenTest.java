package org.schulcloud.mobile.data.model;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class AccessTokenTest {
    private static final String ACCESTOKEN = "OHNOACCESS";

    private AccessToken accessToken;

    @Before
    public void setUp() throws Exception {
        accessToken = createAccesToken();
    }

    public static AccessToken createAccesToken() {
        AccessToken accessToken = new AccessToken();
        accessToken.accessToken = ACCESTOKEN;

        return accessToken;
    }

    @Test
    public void getAccessToken() throws Exception {
        assertEquals(accessToken.getAccessToken(), ACCESTOKEN);
    }

    @Test
    public void setAccessToken() throws Exception {
        accessToken.setAccessToken(ACCESTOKEN);
        assertNotNull(accessToken.getAccessToken());
    }

}