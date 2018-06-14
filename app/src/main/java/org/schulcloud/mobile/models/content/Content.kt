package org.schulcloud.mobile.models.content

import io.realm.RealmList
import io.realm.RealmObject

/**
 * Date: 6/11/2018
 */
open class Content : RealmObject() {
    // text
    var text: String? = null

    // resources
    var resources: RealmList<Resource>? = null

    // geogebra
    var materialId: String? = null

    // etherpad, nexboard
    var title: String? = null
    var description: String? = null
    var url: String? = null
}
