package org.schulcloud.mobile.models.news

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class News : RealmObject() {

    @PrimaryKey
    @SerializedName("_id")
    var id: String = ""

    var schoolId: String? = null
    var title: String? = null
    var content: String? = null
    var createdAt: String? = null
}
