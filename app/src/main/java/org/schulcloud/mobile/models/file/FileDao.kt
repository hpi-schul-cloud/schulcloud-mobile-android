package org.schulcloud.mobile.models.file

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.utils.allAsLiveData

class FileDao(private val realm: Realm) {
    fun files(path: String): LiveData<List<File>> {
        return realm.where(File::class.java)
                .equalTo("path", path)
                .allAsLiveData()
    }

    fun directories(path: String): LiveData<List<Directory>> {
        return realm.where(Directory::class.java)
                .equalTo("path", path)
                .allAsLiveData()
    }
}
