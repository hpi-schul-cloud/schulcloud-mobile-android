package org.schulcloud.mobile.models.material

import androidx.lifecycle.LiveData
import io.realm.Realm
import io.realm.Sort
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.schulcloud.mobile.utils.allAsLiveData
import org.schulcloud.mobile.utils.map

class MaterialDao(private val realm: Realm) {

    fun listCurrentMaterials(): LiveData<List<Material>> {
        val currentDate = LocalDate.now().toString(DateTimeFormat.forPattern("yyyy-MM-dd"))
        return realm.where(Material::class.java)
                .sort("title", Sort.ASCENDING)
                .allAsLiveData()
                .map { materialList ->
                    materialList.filter { it.featuredUntil?.compareTo(currentDate) ?: -1 >= 0 }
                            .toList()
                }
    }

    fun listPopularMaterials(): LiveData<List<Material>> {
        return realm.where(Material::class.java)
                .sort("clickCount", Sort.DESCENDING)
                .allAsLiveData()
                .map { materialList ->
                    materialList.take(3)
                }
    }
}
