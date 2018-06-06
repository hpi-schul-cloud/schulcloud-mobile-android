package org.schulcloud.mobile.models.Homework

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Homework: RealmObject(){

    @PrimaryKey
    @SerializedName("_id")
    var id: String = ""

    var schoold: String? = null
    var teacherId: String? = null
    var name: String? = null
    var courseId: HomeworkCourse? = null
    @SerializedName("private")
    var restricted: Boolean = false

}