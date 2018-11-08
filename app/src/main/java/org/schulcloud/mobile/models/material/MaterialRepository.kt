package org.schulcloud.mobile.models.material

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
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
        val currentDate = LocalDate.now()
        val dateString = currentDate.toString(DateTimeFormat.forPattern("yyyy-MM-dd"))
        RequestJob.Data.with({ listCurrentMaterials(dateString) }, {
            greaterThanOrEqualTo("featuredUntil", currentDate.toDate())
        }).run()

        RequestJob.Data.with({ listPopularMaterials() }, {
            beginGroup()
                    .isNull("featuredUntil")
                    .or()
                    .lessThan("featuredUntil", currentDate.toDate())
                    .endGroup()
        }).run()
    }
}
