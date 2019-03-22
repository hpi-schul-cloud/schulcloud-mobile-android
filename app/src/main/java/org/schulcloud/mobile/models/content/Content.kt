package org.schulcloud.mobile.models.content

import io.realm.RealmList
import io.realm.RealmObject

open class Content : RealmObject() {
    // text
    var text: String? = null

    // resources
    var resources: RealmList<Resource>? = null

    // internal, etherpad, nexboard
    var url: String? = null

    // geogebra
    var materialId: String? = null

    // etherpad, nexboard
    var title: String? = null
    var description: String? = null
}
