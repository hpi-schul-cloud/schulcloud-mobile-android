package org.schulcloud.mobile.models.user

import com.google.gson.annotations.SerializedName
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Account: RealmObject() {
    @PrimaryKey
    @SerializedName("_id")
    var id: String = ""

    var username: String? = null
    var password: String? = null
    var userId: String? = null

    var newPassword: String? = null
    var newPasswordRepeat: String? = null
}