package org.schulcloud.mobile.models.news

import android.arch.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.jobs.ListUserNewsJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.utils.newsDao

object NewsRepository {

    init {
        requestNewsList()
    }

    fun listNews(realm: Realm): LiveRealmData<News>{
        return realm.newsDao().listNews()
    }

    private fun requestNewsList() {
        ListUserNewsJob(object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code:ErrorCode){
            }
        }).run()
    }
}