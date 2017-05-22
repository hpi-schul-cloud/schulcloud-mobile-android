package org.schulcloud.mobile.data.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class FileTest {

    private static final String KEY = "key";
    private static final String NAME = "TestFile.jpg";
    private static final String PATH = "TEST_PATH";
    private static final String SIZE = "200";
    private static final String TYPE = "IMAGE";
    private static final String THUMBNAIL = "TEST_THUMBNAIL";

    private File file;

    @Before
    public void setUp() {
        file = createNewFile();
    }

    @Test
    public void testGetProperties() {
        assertEquals(file.key, KEY);
        assertEquals(file.name, NAME);
        assertEquals(file.path, PATH);
        assertEquals(file.size, SIZE);
        assertEquals(file.type, TYPE);
        assertEquals(file.thumbnail, THUMBNAIL);
    }

    public static File createNewFile() {
        File file = new File();
        file.key = KEY;
        file.name = NAME;
        file.path = PATH;
        file.size = SIZE;
        file.type = TYPE;
        file.thumbnail = THUMBNAIL;

        return file;
    }

}