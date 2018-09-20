package org.schulcloud.mobile.models.user

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.models.base.HasId

open class User : RealmObject(), HasId {
    companion object {
        val PERMISSION_LESSONS_VIEW = "LESSONS_VIEW"
        val PERMISSION_TOOL_NEW_VIEW = "TOOL_NEW_VIEW"
        val PERMISSION_COURSE_EDIT = "COURSE_EDIT"
        val PERMISSION_HOMEWORK_CREATE = "HOMEWORK_CREATE"
        val PERMISSION_FILE_CREATE = "FILE_CREATE"
        val PERMISSION_FILE_MOVE = "FILE_MOVE"
        val PERMISSION_FILE_DELETE = "FILE_DELETE"
        val PERMISSION_FOLDER_CREATE = "FOLDER_CREATE"
        val PERMISSION_FOLDER_DELETE = "FOLDER_DELETE"
        val PERMISSION_TEACHER_CREATE = "TEACHER_CREATE"
        val PERMISSION_STUDENT_CREATE = "STUDENT_CREATE"
        val PERMISSION_BASE_VIEW = "BASE_VIEW"
        val PERMISSION_DASHBOARD_VIEW = "DASHBOARD_VIEW"
        val PERMISSION_TOOL_VIEW = "TOOL_VIEW"
    }

    @PrimaryKey
    @SerializedName("_id")
    override var id: String = ""

    var firstName: String? = null
    var lastName: String? = null
    var email: String? = null
    var schoolId: String? = null
    var displayName: String? = null
    var permissions: RealmList<String> = RealmList()
    var roles: RealmList<String> = RealmList()

    val name get() = "$firstName $lastName"
    val shortName get() = "${firstName?.get(0)}. $lastName"

    fun hasPermission(permission: String): Boolean{
        return permission.contains(permission)
    }
}
