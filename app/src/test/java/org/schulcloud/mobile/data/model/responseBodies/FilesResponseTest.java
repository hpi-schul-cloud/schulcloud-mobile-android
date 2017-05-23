package org.schulcloud.mobile.data.model.responseBodies;


import org.junit.Before;
import org.junit.Test;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.File;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class FilesResponseTest {

    public List<File> files;
    public List<Directory> directories;

    private static final List<File> FILE_LIST = new ArrayList<File>();
    private static final List<Directory> DIRECTORY_LIST = new ArrayList<Directory>();

    private FilesResponse filesResponse;

    @Before
    public void setUp() throws Exception {
        filesResponse = createFilesResponse();
    }

    @Test
    public void testGetProperties() {
        assertEquals(filesResponse.directories, DIRECTORY_LIST);
        assertEquals(filesResponse.files, FILE_LIST);
    }

    public static FilesResponse createFilesResponse() {
        FilesResponse filesResponse = new FilesResponse();
        filesResponse.files = FILE_LIST;
        filesResponse.directories = DIRECTORY_LIST;

        return filesResponse;
    }
}