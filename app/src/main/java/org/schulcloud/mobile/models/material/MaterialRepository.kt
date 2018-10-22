package org.schulcloud.mobile.models.material

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.utils.materialDao

object MaterialRepository {
    fun currentMaterialList(realm: Realm): LiveData<List<Material>> {
        return realm.materialDao().listCurrentMaterials()
    }

    fun popularMaterialList(realm: Realm): LiveData<List<Material>> {
        return realm.materialDao().listPopularMaterials()
    }

    suspend fun syncMaterials() {
        RequestJob.Data.with({listMaterials()}).run()
    }
}
