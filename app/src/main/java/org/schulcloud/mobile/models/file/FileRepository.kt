package org.schulcloud.mobile.models.file

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.models.base.Repository
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.utils.*

object FileRepository : Repository() {
    const val CONTEXT_MY = "my"
    const val CONTEXT_MY_API = "users"
    const val CONTEXT_COURSES = "courses"

    fun files(realm: Realm, refOwnerModel: String, owner: String): LiveData<List<File>> {
        return realm.fileDao().files(refOwnerModel, owner)
    }

    fun directories(realm: Realm, refOwnerModel: String,owner: String): LiveData<List<File>> {
        return realm.fileDao().directories(refOwnerModel, owner)
    }


    suspend fun syncDirectory(refOwnerModel: String, owner: String) {
        RequestJob.Data.with({ listDirectoryContents(refOwnerModel, owner) }).run()
    }


    fun fixPath(path: String): String {
        var fixedPath = path.trimLeadingSlash().ensureTrailingSlash()
        if (path.startsWith(CONTEXT_MY))
            fixedPath = path.replaceRange(0, CONTEXT_MY.length, pathPersonal("").trimTrailingSlash())
        return fixedPath
    }

    fun pathPersonal(path: String? = null): String {
        return combinePath(CONTEXT_MY_API, UserRepository.userId!!, path)
    }

    fun pathCourse(courseId: String, path: String? = null): String {
        return combinePath(CONTEXT_COURSES, courseId, path)
    }
}
