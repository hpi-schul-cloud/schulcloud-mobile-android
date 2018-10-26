package org.schulcloud.mobile.models.material

import androidx.lifecycle.LiveData
import io.realm.Realm
import io.realm.Sort
import org.schulcloud.mobile.utils.allAsLiveData
import org.schulcloud.mobile.utils.map

class MaterialDao(private val realm: Realm) {

    fun listCurrentMaterials(): LiveData<List<Material>> {
        return realm.where(Material::class.java)
                .sort("updatedAt", Sort.DESCENDING)
                .allAsLiveData()
                .map { materialList ->
                    materialList.take(3)
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

