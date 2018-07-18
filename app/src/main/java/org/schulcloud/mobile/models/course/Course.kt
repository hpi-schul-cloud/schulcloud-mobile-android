package org.schulcloud.mobile.models.course

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.utils.HOST

open class Course : RealmObject() {

    @PrimaryKey
    @SerializedName("_id")
    var id: String = ""

    var schoolId: String? = null
    var name: String? = null
    var description: String? = null
    var color: String? = null

    @SerializedName("teacherIds")
    var teachers: RealmList<User>? = null

    val url: String
        get() = "$HOST/courses/$id"
}