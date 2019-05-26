package org.schulcloud.mobile.models.file

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.utils.allAsLiveData

class FileDao(private val realm: Realm) {
    fun files(refOwnerModel: String, owner: String, parent: String?): LiveData<List<File>> {
        return realm.where(File::class.java)
                .equalTo("owner", owner)
                .equalTo("refOwnerModel", refOwnerModel)
                .equalTo("isDirectory", false)
                .equalTo("parent", parent)
                .allAsLiveData()
    }

    fun directories(refOwnerModel: String, owner: String, parent: String?): LiveData<List<File>> {
        return realm.where(File::class.java)
                .equalTo("owner", owner)
                .equalTo("refOwnerModel", refOwnerModel)
                .equalTo("isDirectory", true)
                .equalTo("parent", parent)
                .allAsLiveData()
    }
}
