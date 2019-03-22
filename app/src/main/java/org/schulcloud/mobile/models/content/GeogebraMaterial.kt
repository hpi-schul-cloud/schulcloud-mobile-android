package org.schulcloud.mobile.models.content

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.models.base.HasId

open class GeogebraMaterial : RealmObject(), HasId {
    @PrimaryKey
    override var id: String = ""

    var previewUrl: String? = null
}
