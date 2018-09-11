package org.schulcloud.mobile.models.file

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.models.base.HasId

open class File : RealmObject(), HasId {
    override val id: String
        get() = key

    @PrimaryKey
    var key: String = ""
    var name: String? = null
    var path: String? = null

    var type: String? = null
    var size: Long? = null
    var thumbnail: String? = null
    var flatFileName: String? = null
}
