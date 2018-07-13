package org.schulcloud.mobile.models.event

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Event : RealmObject() {
    companion object {
        const val TYPE_TEMPLATE = "template"
    }

    @PrimaryKey
    @SerializedName("_id")
    var id: String? = null
    var type: String? = null
    var title: String? = null
    var allDay: Boolean? = null
    var start: Long? = null
    var end: Long? = null
    var summary: String? = null
    var location: String? = null
    var included: RealmList<Included>? = null

    @SerializedName("x-sc-courseId")
    var courseId: String? = null
}
