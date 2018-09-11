package org.schulcloud.mobile.models.file

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.models.base.HasId

open class Directory : RealmObject(), HasId {
    override val id: String
        get() = key ?: ""

    @PrimaryKey
    var key: String? = null
    var name: String? = null
    var path: String? = null
}
