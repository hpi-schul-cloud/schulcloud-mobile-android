package org.schulcloud.mobile.models.file

import io.realm.Realm
import org.schulcloud.mobile.jobs.ListDirectoryContentsJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.utils.fileDao

object FileRepository {
    fun files(realm: Realm, path: String): LiveRealmData<File> {
        return realm.fileDao().files(path)
    }

    fun directories(realm: Realm, path: String): LiveRealmData<Directory> {
        return realm.fileDao().directories(path)
    }

    suspend fun syncDirectory(path: String) {
        ListDirectoryContentsJob(path, object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code: ErrorCode) {
            }
        }).run()
    }
}
