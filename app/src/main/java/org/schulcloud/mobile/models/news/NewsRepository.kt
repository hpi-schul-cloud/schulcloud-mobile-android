package org.schulcloud.mobile.models.news

import android.arch.lifecycle.LiveData
import io.realm.Realm
import kotlinx.coroutines.experimental.async
import org.schulcloud.mobile.jobs.ListUserNewsJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.utils.newsDao

object NewsRepository {
    init {
        async {
            syncNews()
        }
    }

    fun news(realm: Realm): LiveData<List<News>> {
        return realm.newsDao().listNews()
    }

    suspend fun syncNews() {
        ListUserNewsJob(object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code: ErrorCode) {
            }
        }).run()
    }
}
