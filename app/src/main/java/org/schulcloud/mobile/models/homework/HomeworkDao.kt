package org.schulcloud.mobile.models.homework

import io.realm.Realm
import io.realm.Sort
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.models.base.RealmObjectLiveData
import org.schulcloud.mobile.utils.asLiveData

class HomeworkDao(private val realm: Realm){

    fun homeworkList() : LiveRealmData<Homework>{
        return realm.where(Homework::class.java)
                .sort("dueDate", Sort.ASCENDING)
                .findAllAsync()
                .asLiveData()
    }

    fun homework(id: String) : RealmObjectLiveData<Homework>{
        return realm.where(Homework::class.java)
                .sort("dueDate", Sort.ASCENDING)
                .equalTo("id", id)
                .findFirstAsync()
                .asLiveData()
    }
}
