package org.schulcloud.mobile.models.file

import androidx.lifecycle.LiveData
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.Sort
import org.schulcloud.mobile.utils.allAsLiveData

class FileDao(private val realm: Realm) {
    fun files(owner: String, parent: String?): LiveData<List<File>> {
        return directoryContentsQuery(owner, parent, false)
                .allAsLiveData()
    }

    fun directories(owner: String, parent: String?): LiveData<List<File>> {
        return directoryContentsQuery(owner, parent, true)
                .allAsLiveData()
    }

    fun directory(id: String): File? {
        return realm.where(File::class.java)
                .equalTo("isDirectory", true)
                .equalTo("id", id)
                .findFirst()
    }

    private fun directoryContentsQuery(
            owner: String,
            parent: String?,
            isDirectory: Boolean): RealmQuery<File> {
        return realm.where(File::class.java)
                .equalTo("owner", owner)
                .equalTo("parent", parent)
                .equalTo("isDirectory", isDirectory)
                .sort("type", Sort.ASCENDING, "name", Sort.ASCENDING)
    }
}
