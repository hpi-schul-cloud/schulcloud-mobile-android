package org.schulcloud.mobile.models.notifications

import io.realm.Realm
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.models.base.RealmObjectLiveData
import org.schulcloud.mobile.utils.asLiveData

class DeviceDao(private val realm: Realm){

    fun devices(): LiveRealmData<Device>{
        return realm.where(Device::class.java)
                .findAllAsync()
                .asLiveData()
    }

    fun device(id: String): RealmObjectLiveData<Device> {
        return realm.where(Device::class.java)
                .equalTo("_id",id)
                .findFirstAsync()
                .asLiveData()
    }

}