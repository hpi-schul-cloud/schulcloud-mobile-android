package org.schulcloud.mobile.models.user

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class User : RealmObject() {

    @PrimaryKey
    @SerializedName("_id")
    var id: String = ""

    var firstName: String? = null
    var lastName: String? = null
    var email: String? = null
    var schoolId: String? = null
    var displayName: String? = null

    val shortName
        get() = "${firstName?.get(0)}. $lastName"
}
