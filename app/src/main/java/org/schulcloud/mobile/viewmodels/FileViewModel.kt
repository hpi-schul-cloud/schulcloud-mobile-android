package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.utils.getPathParts

class FileViewModel(val path: String) : ViewModel() {
       val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val directories: LiveData<List<File>> = FileRepository.directories(realm, refOwnerModel, owner)
    val files: LiveData<List<File>> = FileRepository.files(realm, refOwnerModel, owner)

    val refOwnerModel: String
        get() = path.getPathParts()[0]
    val owner: String
        get() = path.getPathParts()[1]
}
