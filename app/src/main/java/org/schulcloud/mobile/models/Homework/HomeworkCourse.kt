package org.schulcloud.mobile.models.Homework

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class HomeworkCourse: RealmObject() {

    @PrimaryKey
    @SerializedName("_id")
    var id: String = ""

    var schoolId: String? = null
    var name: String? = null
    var color: String? = null
}