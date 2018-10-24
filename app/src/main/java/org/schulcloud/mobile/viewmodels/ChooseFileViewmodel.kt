package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.ViewModel
import org.schulcloud.mobile.models.file.Directory
import org.schulcloud.mobile.models.file.File

class ChooseFileViewmodel: ViewModel() {
    var path: String = "/storage/self/primary/"
    var directories: List<Directory> = listOf()
    var files: List<File> = listOf()
}
