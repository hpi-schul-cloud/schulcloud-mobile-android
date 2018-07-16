package org.schulcloud.mobile.models.content

import io.realm.RealmObject

/**
 * Date: 6/11/2018
 */
open class Resource : RealmObject() {
    var url: String? = null
    var client: String? = null
    var title: String? = null
    var description: String? = null
}
