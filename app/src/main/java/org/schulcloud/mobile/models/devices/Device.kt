package org.schulcloud.mobile.models.devices

import com.google.gson.annotations.SerializedName
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

class Device: RealmObject() {

    @PrimaryKey
    @SerializedName("_id")
    var id: String = ""

    var name: String = ""
    var token: String = ""

}