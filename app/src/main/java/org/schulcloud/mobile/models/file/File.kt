package org.schulcloud.mobile.models.file

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.models.base.HasId

open class File : RealmObject(), HasId {
    @PrimaryKey
    @SerializedName("_id")
    override var id: String = ""

    var name: String? = null
    var path: String? = null

    var type: String? = null
    var size: Long? = null
    var thumbnail: String? = null
    var refOwnerModel: String? = null
    var owner: String? = null
    var storageFileName: String? = null
    var isDirectory: Boolean? = null
}
