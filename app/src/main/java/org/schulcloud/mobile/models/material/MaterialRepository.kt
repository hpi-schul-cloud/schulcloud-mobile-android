package org.schulcloud.mobile.models.material

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.utils.materialDao

object MaterialRepository {
    fun materialList(realm: Realm): LiveData<List<Material>> {
        return realm.materialDao().listMaterials()
    }

    suspend fun syncMaterials() {
        RequestJob.Data.with({listMaterials()}).run()
    }
}
