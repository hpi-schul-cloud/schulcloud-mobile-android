package org.schulcloud.mobile.models.material

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.utils.allAsLiveData

class MaterialDao(private val realm: Realm) {

    fun listMaterials(): LiveData<List<Material>>{
        return realm.where(Material::class.java)
                .allAsLiveData()
    }
}
