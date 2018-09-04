package org.schulcloud.mobile.models.homework

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.jobs.GetHomeworkJob
import org.schulcloud.mobile.jobs.ListUserHomeworkJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.utils.homeworkDao

object HomeworkRepository {
    fun homeworkList(realm: Realm): LiveData<List<Homework>> {
        return realm.homeworkDao().homeworkList()
    }

    fun openHomeworkForNextWeek(realm: Realm): LiveData<List<Homework>> {
        return realm.homeworkDao().openHomeworkForNextWeek()
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
        GetHomeworkJob(homeworkId, RequestJobCallback()).run()
    }
}
