package org.schulcloud.mobile.models.file

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.models.base.Repository
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.utils.*

object FileRepository : Repository() {
    const val CONTEXT_MY = "my"
    const val CONTEXT_MY_API = "user"
    const val CONTEXT_COURSE = "course"

    val user: String
        get() = UserRepository.userId!!

    fun files(realm: Realm, owner: String, parent: String?): LiveData<List<File>> {
        return realm.fileDao().files(owner, parent)
    }

    fun directories(realm: Realm, owner: String, parent: String?): LiveData<List<File>> {
        return realm.fileDao().directories(owner, parent)
    }

    fun directory(realm: Realm, id: String): File? {
        return realm.fileDao().directory(id)
    }

    suspend fun syncDirectory(owner: String, parent: String?) {
        RequestJob.Data.with({ listDirectoryContents(owner, parent) }, {
            equalTo("isDirectory", false)
        }).run()
    }

    suspend fun syncDirectoriesForOwner(owner: String) {
        RequestJob.Data.with({ listDirectoriesForOwner(owner) }, {
            equalTo("isDirectory", true)
        }).run()
    }
}
