package org.schulcloud.mobile.models.content

import io.realm.Realm
import org.schulcloud.mobile.models.base.RealmObjectLiveData
import org.schulcloud.mobile.utils.asLiveData

/**
 * Date: 6/18/2018
 */
class ContentDao(private val realm: Realm) {
    fun geogebraMaterial(id: String): RealmObjectLiveData<GeogebraMaterial> {
        return realm.where(GeogebraMaterial::class.java)
                .equalTo("id", id)
                .findFirstAsync()
                .asLiveData()
    }
}
