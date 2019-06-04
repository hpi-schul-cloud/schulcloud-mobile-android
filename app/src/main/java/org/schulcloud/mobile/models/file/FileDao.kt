package org.schulcloud.mobile.models.file

import androidx.lifecycle.LiveData
import io.realm.Realm
import io.realm.Sort
import org.schulcloud.mobile.utils.allAsLiveData

class FileDao(private val realm: Realm) {
    fun files(owner: String, parent: String?): LiveData<List<File>>
            = directoryContents(owner, parent, false)

    fun directories(owner: String, parent: String?): LiveData<List<File>>
        = directoryContents(owner, parent, true)


    fun directory(id: String): File? {
        return realm.where(File::class.java)
                .equalTo("isDirectory", true)
                .equalTo("id", id)
                .findFirst()
    }

    private fun directoryContents(
            owner: String,
            parent: String?,
            isDirectory: Boolean): LiveData<List<File>> {
        return realm.where(File::class.java)
                .equalTo("owner", owner)
                .equalTo("parent", parent)
                .equalTo("isDirectory", isDirectory)
                .sort("type", Sort.ASCENDING, "name", Sort.ASCENDING)
                .allAsLiveData()
    }
}
