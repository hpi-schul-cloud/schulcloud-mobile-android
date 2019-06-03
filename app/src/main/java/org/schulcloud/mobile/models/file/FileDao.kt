package org.schulcloud.mobile.models.file

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.utils.allAsLiveData

class FileDao(private val realm: Realm) {
    fun files(owner: String, parent: String?): LiveData<List<File>> {
        return realm.where(File::class.java)
                .equalTo("owner", owner)
                .equalTo("isDirectory", false)
                .equalTo("parent", parent)
                .allAsLiveData()
    }

    fun directories(owner: String, parent: String?): LiveData<List<File>> {
        return realm.where(File::class.java)
                .equalTo("owner", owner)
                .equalTo("isDirectory", true)
                .equalTo("parent", parent)
                .allAsLiveData()
    }

    fun directory(id: String): File? {
        return realm.where(File::class.java)
                .equalTo("isDirectory", true)
                .equalTo("id", id)
                .findFirst()
    }
}
