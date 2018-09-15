package org.schulcloud.mobile.models.homework.submission

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.models.base.HasId

open class Comment : RealmObject(), HasId {

    @PrimaryKey
    @SerializedName("_id")
    override var id: String = ""

    var submissionId: String? = null
    var author: String? = null
    var comment: String? = null
    var createdAt: String? = null

}
