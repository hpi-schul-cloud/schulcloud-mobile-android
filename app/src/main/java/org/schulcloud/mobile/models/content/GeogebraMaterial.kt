package org.schulcloud.mobile.models.content

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Date: 6/18/2018
 */
open class GeogebraMaterial : RealmObject() {
    @PrimaryKey
    lateinit var id: String

    var previewUrl: String? = null
}
