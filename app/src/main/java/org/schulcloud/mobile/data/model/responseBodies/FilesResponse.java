package org.schulcloud.mobile.data.model.responseBodies;

import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.File;

import java.util.List;

/**
 * Created by niklaskiefer on 21.04.17.
 */

public class FilesResponse {
    public List<File> files;
    public List<Directory> directories;
}
