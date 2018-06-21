package org.schulcloud.mobile.models.content

import io.realm.RealmObject

/**
 * Date: 6/11/2018
 */
open class ContentWrapper : RealmObject() {
    companion object {
        const val COMPONENT_TEXT = "text"
        const val COMPONENT_RESOURCES = "resources"
        const val COMPONENT_INTERNAL = "internal"
        const val COMPONENT_GEOGEBRA = "geoGebra"
        const val COMPONENT_ETHERPAD = "Etherpad"
        const val COMPONENT_NEXBOARD = "neXboard"
    }

    var component: String? = null
    var title: String? = null
    var hidden: Boolean? = null
    var content: Content? = null
}
