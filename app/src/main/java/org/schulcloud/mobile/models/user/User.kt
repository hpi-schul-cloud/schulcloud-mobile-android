package org.schulcloud.mobile.models.user

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.models.base.HasId

open class User : RealmObject(), HasId {

    val PERMISSION_FOLDER_CREATE = "FOLDER_CREATE"
    val PERMISSION_FOLDER_DELETE = "FOLDER_DELETE"
    val PERMISSION_FILE_CREATE = "FILE_CREATE"
    val PERMISSION_FILE_DELETE = "FILE_DELETE"

    @PrimaryKey
    @SerializedName("_id")
    override var id: String = ""

    var firstName: String? = null
    var lastName: String? = null
    var email: String? = null
    var schoolId: String? = null
    var displayName: String? = null
    var permissions: RealmList<String>? = null

    fun hasPermission(permission: String): Boolean{
        if(permission.isNullOrEmpty())
            return false
        permissions?.forEach {
            if(permission == it)
                return true
        }
        return false
    }

    val name get() = "$firstName $lastName"
    val shortName get() = "${firstName?.get(0)}. $lastName"
}
