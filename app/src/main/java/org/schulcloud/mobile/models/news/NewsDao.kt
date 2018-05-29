package org.schulcloud.mobile.models.news

import io.realm.Realm
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.utils.asLiveData

class NewsDao (private val realm: Realm) {

    fun listNews(): LiveRealmData<News>{
        return realm.where(News::class.java)
                .findAllAsync()
                .asLiveData()
    }
}