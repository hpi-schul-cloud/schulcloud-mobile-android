package org.schulcloud.mobile.models.homework

import android.arch.lifecycle.LiveData
import io.realm.Realm
import kotlinx.coroutines.experimental.async
import org.schulcloud.mobile.jobs.GetHomeworkJob
import org.schulcloud.mobile.jobs.ListUserHomeworkJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.utils.homeworkDao

object HomeworkRepository {
    init {
        async {
            syncHomeworkList()
        }
    }

    fun homeworkList(realm: Realm): LiveData<List<Homework>> {
        return realm.homeworkDao().homeworkList()
    }

    fun homework(realm: Realm, id: String): LiveData<Homework?> {
        return realm.homeworkDao().homework(id)
    }

    suspend fun syncHomeworkList() {
        ListUserHomeworkJob(object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code: ErrorCode) {
            }
        }).run()
    }

    suspend fun syncHomework(homeworkId: String) {
        GetHomeworkJob(homeworkId, object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code: ErrorCode) {
            }
        }).run()
    }
}
