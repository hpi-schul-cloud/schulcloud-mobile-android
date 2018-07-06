package org.schulcloud.mobile.models.file

import io.realm.Realm
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.utils.asLiveData

class FileDao(private val realm: Realm) {
    fun files(path: String): LiveRealmData<File> {
        return realm.where(File::class.java)
                .equalTo("path", path)
                .findAllAsync()
                .asLiveData()
    }

    fun directories(path: String): LiveRealmData<Directory> {
        return realm.where(Directory::class.java)
                .equalTo("path", path)
                .findAllAsync()
                .asLiveData()
    }
}
