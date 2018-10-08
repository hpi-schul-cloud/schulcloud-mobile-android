package org.schulcloud.mobile.models.material

import io.realm.RealmObject

open class Material : RealmObject() {
    var providerName: String? = null
    var url: String? = null
    var title: String? = null
    var description: String? = null
    var thumbnail: String? = null
    var clickCount: Int? = null
    var createdAt: String? = null

    var tags: List<String>? = null
}
