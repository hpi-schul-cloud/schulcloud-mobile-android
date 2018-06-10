package org.schulcloud.mobile.models.topic

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


/**
 * Date: 6/10/2018
 */
open class Topic : RealmObject() {

    @PrimaryKey
    @SerializedName("_id")
    var id: String? = null

    var name: String? = null
    var description: String? = null
    var date: String? = null
    var time: String? = null
    var courseId: String? = null
    var hidden: Boolean? = null
}
