package org.schulcloud.mobile.models.news

import io.realm.Realm
import kotlinx.coroutines.experimental.async
import org.schulcloud.mobile.jobs.ListUserNewsJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.utils.newsDao

object NewsRepository {

    init {
        async {
            syncNews()
        }
    }

    fun news(realm: Realm): LiveRealmData<News>{
        return realm.newsDao().listNews()
    }

    suspend fun syncNews() {
        ListUserNewsJob(object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code:ErrorCode){
            }
        }).run()
    }
}
