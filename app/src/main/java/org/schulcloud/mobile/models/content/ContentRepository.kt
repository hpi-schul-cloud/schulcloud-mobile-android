package org.schulcloud.mobile.models.content

import io.realm.Realm
import org.schulcloud.mobile.jobs.GetGeogebraMaterialJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.base.RealmObjectLiveData
import org.schulcloud.mobile.utils.contentDao

/**
 * Date: 6/18/2018
 */
object ContentRepository {
    fun geogebraPreviewUrl(realm: Realm, id: String): RealmObjectLiveData<GeogebraMaterial> {
        requestGeogebraMaterial(id)
        return realm.contentDao().geogebraMaterial(id)
    }

    private fun requestGeogebraMaterial(materialId: String) {
        GetGeogebraMaterialJob(materialId, object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code: ErrorCode) {
            }
        }).run()
    }
}
