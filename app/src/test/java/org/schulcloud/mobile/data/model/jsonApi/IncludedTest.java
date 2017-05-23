package org.schulcloud.mobile.data.model.jsonApi;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class IncludedTest {
    private static final String TYPE = "TYPE";
    private static final String ID = "ID";
    private static final IncludedAttributes ATTRIBUTES = new IncludedAttributes();

    private Included included;

    @Before
    public void setUp() throws Exception {
        included = newIncluded();
    }

    public static Included newIncluded() {
        Included included = new Included();
        included.setAttributes(ATTRIBUTES);
        included.setId(ID);
        included.setType(TYPE);

        return included;
    }

    @Test
    public void getType() throws Exception {
        assertEquals(included.getType(), TYPE);
    }

    @Test
    public void setType() throws Exception {
        included.setType(TYPE);
        assertNotNull(included.getType());
    }

    @Test
    public void getId() throws Exception {
        assertEquals(included.getId(), ID);
    }

    @Test
    public void setId() throws Exception {
        included.setId(ID);
        assertNotNull(included.getId());
    }

    @Test
    public void getAttributes() throws Exception {
        assertEquals(included.getAttributes(), ATTRIBUTES);
    }

    @Test
    public void setAttributes() throws Exception {
        included.setAttributes(ATTRIBUTES);
        assertNotNull(included.getAttributes());
    }

}