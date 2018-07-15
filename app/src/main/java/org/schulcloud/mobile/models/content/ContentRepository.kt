package org.schulcloud.mobile.models.content

import android.arch.lifecycle.LiveData
import io.realm.Realm
import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.jobs.GetGeogebraMaterialJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.utils.contentDao

/**
 * Date: 6/18/2018
 */
object ContentRepository {
    fun geogebraPreviewUrl(realm: Realm, id: String): LiveData<GeogebraMaterial?> {
        launch {
            requestGeogebraMaterial(id)
        }
        return realm.contentDao().geogebraMaterial(id)
    }

    private suspend fun requestGeogebraMaterial(materialId: String) {
        GetGeogebraMaterialJob(materialId, object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code: ErrorCode) {
            }
        }).run()
    }
}
