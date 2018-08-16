package org.schulcloud.mobile.models.news

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.jobs.GetNewsJob
import org.schulcloud.mobile.jobs.ListUserNewsJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.utils.newsDao

object NewsRepository {
    fun newsList(realm: Realm): LiveData<List<News>> {
        return realm.newsDao().listNews()
    }

    fun news(realm: Realm, id: String): LiveData<News?> {
        return realm.newsDao().news(id)
    }

    suspend fun syncNews() {
        ListUserNewsJob(RequestJobCallback()).run()
    }
    suspend fun syncNews(id: String) {
        GetNewsJob(id, RequestJobCallback()).run()
    }
}
