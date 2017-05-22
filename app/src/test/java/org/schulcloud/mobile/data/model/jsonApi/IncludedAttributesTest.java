package org.schulcloud.mobile.data.model.jsonApi;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class IncludedAttributesTest {
    private static final String FREQ = "FREQ";
    private static final String UNTIL = "UNTIL";
    private static final String WKST = "WKST";

    private IncludedAttributes includedAttributes;

    @Before
    public void setUp() throws Exception {
        includedAttributes = newIncludedAttributes();
    }

    public static IncludedAttributes newIncludedAttributes() {
        IncludedAttributes includedAttributes = new IncludedAttributes();
        includedAttributes.setFreq(FREQ);
        includedAttributes.setUntil(UNTIL);
        includedAttributes.setWkst(WKST);

        return includedAttributes;
    }

    @Test
    public void getFreq() throws Exception {
        assertEquals(includedAttributes.getFreq(), FREQ);
    }

    @Test
    public void setFreq() throws Exception {
        includedAttributes.setFreq(FREQ);
        assertNotNull(includedAttributes.getFreq());
    }

    @Test
    public void getUntil() throws Exception {
        assertEquals(includedAttributes.getUntil(), UNTIL);
    }

    @Test
    public void setUntil() throws Exception {
        includedAttributes.setUntil(UNTIL);
        assertNotNull(includedAttributes.getUntil());
    }

    @Test
    public void getWkst() throws Exception {
        assertEquals(includedAttributes.getWkst(), WKST);
    }

    @Test
    public void setWkst() throws Exception {
        includedAttributes.setWkst(WKST);
        assertNotNull(includedAttributes.getWkst());
    }

}