package org.schulcloud.mobile.models.content

import androidx.lifecycle.LiveData
import android.util.Log
import io.realm.Realm
import kotlinx.coroutines.experimental.async
import org.schulcloud.mobile.jobs.GetGeogebraMaterialJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.utils.contentDao

/**
 * Date: 6/18/2018
 */
object ContentRepository {
    fun geogebraPreviewUrl(realm: Realm, id: String): LiveData<GeogebraMaterial?> {
        async {
            requestGeogebraMaterial(id)
        }
        return realm.contentDao().geogebraMaterial(id)
    }

    private suspend fun requestGeogebraMaterial(materialId: String) {
        GetGeogebraMaterialJob(materialId, object : RequestJobCallback() {
            override fun onSuccess() {
                Log.d("", "Get url")
            }

            override fun onError(code: ErrorCode) {
            }
        }).run()
    }
}
