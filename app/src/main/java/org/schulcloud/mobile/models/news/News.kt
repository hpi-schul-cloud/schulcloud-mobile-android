package org.schulcloud.mobile.models.news

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.models.base.HasId

open class News : RealmObject(), HasId {

    @PrimaryKey
    @SerializedName("_id")
    override var id: String = ""

    var schoolId: String? = null
    var title: String? = null
    var content: String? = null
    var createdAt: String? = null
}
