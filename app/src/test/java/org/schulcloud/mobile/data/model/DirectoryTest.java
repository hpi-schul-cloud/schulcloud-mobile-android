package org.schulcloud.mobile.data.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DirectoryTest {
    private static final String NAME = "NAME";

    private Directory directory;

    @Before
    public void setUp() {
        directory = createNewDirectory();
    }

    @Test
    public void testGetProperties() {
        assertEquals(directory.name, NAME);

    }

    public static Directory createNewDirectory() {
        Directory directory = new Directory();
        directory.name = NAME;

        return directory;
    }
}