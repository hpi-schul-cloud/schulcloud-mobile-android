package org.schulcloud.mobile.models.content

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.utils.firstAsLiveData

/**
 * Date: 6/18/2018
 */
class ContentDao(private val realm: Realm) {
    fun geogebraMaterial(id: String): LiveData<GeogebraMaterial?> {
        return realm.where(GeogebraMaterial::class.java)
                .equalTo("id", id)
                .firstAsLiveData()
    }
}
