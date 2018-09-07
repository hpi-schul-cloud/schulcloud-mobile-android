package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.file.Directory
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.models.user.UserRepository

class FileViewModel(path_: String) : ViewModel() {
    val path = FileRepository.fixPath(path_)

    val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val directories: LiveData<List<Directory>> = FileRepository.directories(realm, path)
    val files: LiveData<List<File>> = FileRepository.files(realm, path)
    val user: LiveData<User> = UserRepository.currentUser()
}
