package org.schulcloud.mobile.data.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CurrentUserTest {
    private static final String _ID = "key";
    private static final String FIRSTNAME = "Ernst";
    private static final String LASTNAME = "Haft";
    private static final String EMAIL = "ernst@haft.bla";
    private static final String SCHOOLID = "123214123";
    private static final String DISPLAYNAME = "Ernst Haft";

    private CurrentUser user;

    @Before
    public void setUp() {
        user = createNewUser();
    }

    public static CurrentUser createNewUser()  {
        CurrentUser user = new CurrentUser();
        user.set_id(_ID);
        user.setDisplayName(DISPLAYNAME);
        user.setSchoolId(SCHOOLID);
        user.setLastName(LASTNAME);
        user.setFirstName(FIRSTNAME);
        user.setEmail(EMAIL);

        return user;
    }

    @Test
    public void get_id() throws Exception {
        assertEquals(user.get_id(), _ID);
    }

    @Test
    public void set_id() throws Exception {
        user.set_id(_ID);
        assertNotNull(user.get_id());
    }

    @Test
    public void getFirstName() throws Exception {
        assertEquals(user.getFirstName(), FIRSTNAME);
    }

    @Test
    public void setFirstName() throws Exception {
        user.setFirstName(FIRSTNAME);
        assertNotNull(user.getFirstName());
    }

    @Test
    public void getLastName() throws Exception {
        assertEquals(user.getLastName(), LASTNAME);
    }

    @Test
    public void setLastName() throws Exception {
        user.setLastName(LASTNAME);
        assertNotNull(user.getLastName());
    }

    @Test
    public void getEmail() throws Exception {
        assertEquals(user.getEmail(), EMAIL);
    }

    @Test
    public void setEmail() throws Exception {
        user.setEmail(EMAIL);
        assertNotNull(user.getEmail());
    }

    @Test
    public void getSchoolId() throws Exception {
        assertEquals(user.getSchoolId(), SCHOOLID);
    }

    @Test
    public void setSchoolId() throws Exception {
        user.setSchoolId(SCHOOLID);
        assertNotNull(user.getSchoolId());
    }

    @Test
    public void getDisplayName() throws Exception {
        assertEquals(user.getDisplayName(), DISPLAYNAME);
    }

    @Test
    public void setDisplayName() throws Exception {
        user.setDisplayName(DISPLAYNAME);
        assertNotNull(user.getDisplayName());
    }

}