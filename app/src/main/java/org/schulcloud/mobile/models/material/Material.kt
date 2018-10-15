package org.schulcloud.mobile.models.material

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.models.base.HasId

open class Material : RealmObject(), HasId {
    @PrimaryKey
    @SerializedName("_id")
    override var id: String = ""

    var providerName: String? = null
    var url: String? = null
    var title: String? = null
    var description: String? = null
    var thumbnail: String? = null
    var clickCount: Int? = null
    var createdAt: String? = null
    var updatedAt: String? = null

    //var tags: List<String>? = null
}
