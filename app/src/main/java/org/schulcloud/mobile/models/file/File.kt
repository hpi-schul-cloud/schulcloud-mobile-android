package org.schulcloud.mobile.models.file

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.models.base.HasId
import org.schulcloud.mobile.models.base.RealmString


open class File : RealmObject(), HasId {
    override val id: String
        get() = key

    @PrimaryKey
    var key: String = ""
    var path: String? = null
    var name: String? = null

    var type: String? = null
    var size: Long? = null
    var thumbnail: String? = null
    var flatFileName: String? = null
    var permissions: RealmList<FilePermissions>? = null
}

open class FilePermissions : RealmObject() {
    companion object {
        const val PERMISSION_READ = "can-read"
        const val PERMISSION_WRITE = "can-write"
    }

    var userId: String = ""
    var permissions: RealmList<RealmString>? = null
}
