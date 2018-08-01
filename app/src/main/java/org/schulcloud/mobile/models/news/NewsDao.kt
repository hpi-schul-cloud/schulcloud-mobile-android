package org.schulcloud.mobile.models.news

import androidx.lifecycle.LiveData
import io.realm.Realm
import io.realm.Sort
import org.schulcloud.mobile.utils.asLiveData

class NewsDao(private val realm: Realm) {

    fun listNews(): LiveData<List<News>> {
        return realm.where(News::class.java)
                .sort("createdAt", Sort.DESCENDING)
                .findAllAsync()
                .asLiveData()
    }
}
