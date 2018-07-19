package org.schulcloud.mobile.models.homework

import io.realm.Realm
import kotlinx.coroutines.experimental.async
import org.schulcloud.mobile.jobs.ListUserHomeworkJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.models.base.RealmObjectLiveData
import org.schulcloud.mobile.utils.homeworkDao

object HomeworkRepository {

    init {
        async {
            syncHomeworkList()
        }
    }


    fun homeworkList(realm: Realm): LiveRealmData<Homework>{
        return realm.homeworkDao().homeworkList()
    }

    fun homework(realm: Realm, id: String): RealmObjectLiveData<Homework>{
        return realm.homeworkDao().homework(id)
    }

    suspend fun syncHomeworkList() {
        ListUserHomeworkJob(object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code:ErrorCode){
            }
        }).run()
    }
}
