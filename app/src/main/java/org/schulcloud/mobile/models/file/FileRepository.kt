package org.schulcloud.mobile.models.file

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.jobs.CreateDirectoryJob
import org.schulcloud.mobile.jobs.ListDirectoryContentsJob
import org.schulcloud.mobile.jobs.UploadFileJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.utils.*

object FileRepository {
    const val CONTEXT_MY = "my"
    const val CONTEXT_MY_API = "users"
    const val CONTEXT_COURSES = "courses"

    fun files(realm: Realm, path: String): LiveData<List<File>> {
        return realm.fileDao().files(path)
    }

    fun directories(realm: Realm, path: String): LiveData<List<Directory>> {
        return realm.fileDao().directories(path)
    }


    suspend fun syncDirectory(path: String) {
        ListDirectoryContentsJob(path).run()
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

    fun uploadFile(file: java.io.File, signedUrl: SignedUrlResponse) {
        UploadFileJob(file,signedUrl,object: RequestJobCallback(){
            override fun onSuccess() {}
            override fun onError(code: ErrorCode) {}
        })
    }

    fun persistFile(signedUrl: SignedUrlResponse, name: String, fileType: String, fileSize: Long){
        var file = File()
        file.name = name
        file.flatFileName = signedUrl.header?.metaFlatName
        file.type = fileType
        file.size = fileSize
        file.path = signedUrl.header?.metaPath + name
        file.thumbnail = signedUrl.header?.metaThumbnail
    }

    suspend fun createDirectory(path: String){
        CreateDirectoryJob(path,object : RequestJobCallback(){
            override fun onSuccess() {}
            override fun onError(code: ErrorCode) {}
        }).run()
    }

    suspend fun deleteFile(fileId: String){
        UserRepository
    }
}
