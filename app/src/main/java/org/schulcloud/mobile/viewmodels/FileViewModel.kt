package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.file.FileRepository

class FileViewModel(val refOwnerModel: String, val owner: String, val parent: String?) : ViewModel() {
       val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val directories: LiveData<List<File>> = FileRepository.directories(realm, refOwnerModel, owner, parent)
    val files: LiveData<List<File>> = FileRepository.files(realm, refOwnerModel, owner, parent)
}
