package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.utils.getPathParts

class FileViewModel(path_: String) : ViewModel() {
    val path = FileRepository.fixPath(path_)

    val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val directories: LiveData<List<File>> = FileRepository.directories(realm, refOwnerModel, owner)
    val files: LiveData<List<File>> = FileRepository.files(realm, refOwnerModel, owner)

    val refOwnerModel: String
        get() = if (path.startsWith(FileRepository.CONTEXT_COURSES)) "course" else "user"
    val owner: String
        get() = path.getPathParts()[1]
}
