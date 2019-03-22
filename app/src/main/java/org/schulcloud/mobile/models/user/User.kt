package org.schulcloud.mobile.models.user

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.models.base.HasId

open class User : RealmObject(), HasId {

    @PrimaryKey
    @SerializedName("_id")
    override var id: String = ""

    var firstName: String? = null
    var lastName: String? = null
    var email: String? = null
    var schoolId: String? = null
    var displayName: String? = null

    val name get() = "$firstName $lastName"
    val shortName get() = "${firstName?.get(0)}. $lastName"
}
