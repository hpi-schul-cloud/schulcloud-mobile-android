package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.models.file.Directory
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.utils.getPathParts

/**
 * Date: 7/6/2018
 */
class FileViewModel(path_: String) : ViewModel() {
    val path = FileRepository.fixPath(path_)

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val directories: LiveRealmData<Directory> = FileRepository.directories(realm, path)
    val files: LiveRealmData<File> = FileRepository.files(realm, path)
}
