package org.schulcloud.mobile.models.homework

import android.arch.lifecycle.LiveData
import io.realm.Realm
import io.realm.RealmResults
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.utils.asLiveData

class HomeworkDao(private val realm: Realm){

    fun listHomework() : LiveRealmData<Homework>{
        // TODO: sorting
        return realm.where(Homework::class.java)
                .findAllAsync()
                .asLiveData()
    }

}