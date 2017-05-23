package org.schulcloud.mobile.data.model.requestBodies;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CredentialsTest {
    private static final String USERNAME = "ERNST";
    private static final String PASSWORD = "1234567890";

    private Credentials credentials;

    @Before
    public void setUp() {
        credentials = createNewCredentials();
    }

    @Test
    public void testGetProperties() {
        assertEquals(credentials.username, USERNAME);
        assertEquals(credentials.password, PASSWORD);
    }

    public static Credentials createNewCredentials() {
        Credentials credentials = new Credentials(USERNAME, PASSWORD);

        return credentials;
    }


}