package org.schulcloud.mobile.models.homework

import io.realm.Realm
import org.schulcloud.mobile.jobs.ListUserHomeworkJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.utils.homeworkDao

object HomeworkRepository {

    init {
        requestHomeworkList()
    }

    fun listHomework(realm: Realm): LiveRealmData<Homework>{
        return realm.homeworkDao().listHomework()
    }

    private fun requestHomeworkList() {
        ListUserHomeworkJob(object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code:ErrorCode){
            }
        }).run()
    }
}