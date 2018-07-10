package org.schulcloud.mobile.models.devices

import com.google.gson.annotations.SerializedName
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Device: RealmObject() {

    @PrimaryKey
    @SerializedName("_id")
    var id: String = ""

    var token: String? = null
    var type: String? = null
    var service: String? = null
    var name: String? = null
    var OS: String? = null
    var state: String? = null
    var updatedAt: String? = null
    var createdAt: String? = null
    var active: String? = null

}