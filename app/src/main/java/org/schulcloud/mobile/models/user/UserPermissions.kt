package org.schulcloud.mobile.models.user

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserPermissions: RealmObject(){
    companion object {
        val PERMISSION_FOLDER_CREATE = "FOLDER_CREATE"
        val PERMISSION_FOLDER_DELETE = "FOLDER_DELETE"
        val PERMISSION_FILE_CREATE = "FILE_CREATE"
        val PERMISSION_FILE_DELETE = "FILE_DELETE"
    }

    @PrimaryKey
    @SerializedName("_id")
    var id: String = ""
    var permissions: RealmList<String>? = null
    var userId: String = ""
}
