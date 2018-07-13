package org.schulcloud.mobile.models.event

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class Included : RealmObject() {
    var type: String? = null
    var id: String? = null
    var attributes: IncludedAttributes? = null
}

open class IncludedAttributes : RealmObject() {
    companion object {
        const val FREQ_WEEKLY = "WEEKLY"
        const val FREQ_DAILY = "DAILY"
    }

    var freq: String? = null
    var until: String? = null
    @SerializedName("wkst")
    var weekday: String? = null
}
