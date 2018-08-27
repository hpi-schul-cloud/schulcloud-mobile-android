package org.schulcloud.mobile.models.content

import androidx.lifecycle.LiveData
import io.realm.Realm
import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.jobs.GetGeogebraMaterialJob
import org.schulcloud.mobile.utils.contentDao

object ContentRepository {
    fun geogebraPreviewUrl(realm: Realm, id: String): LiveData<GeogebraMaterial?> {
        launch { requestGeogebraMaterial(id) }
        return realm.contentDao().geogebraMaterial(id)
    }

    private suspend fun requestGeogebraMaterial(materialId: String) {
        GetGeogebraMaterialJob(materialId).run()
    }
}
