package org.schulcloud.mobile.models.topic

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.models.base.HasId
import org.schulcloud.mobile.models.content.ContentWrapper
import org.schulcloud.mobile.utils.HOST

open class Topic : RealmObject(), HasId {

    @PrimaryKey
    @SerializedName("_id")
    override var id: String = ""

    var courseId: String? = null
    var name: String? = null
    var time: String? = null
    var date: String? = null
    @Suppress("unused")
    var position: Int? = null
    var contents: RealmList<ContentWrapper>? = null

    val url: String
        get() = "$HOST/courses/$courseId/topics/$id"
}
