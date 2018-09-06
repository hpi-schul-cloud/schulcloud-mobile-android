package org.schulcloud.mobile.models.news

import androidx.lifecycle.LiveData
import io.realm.Realm
import io.realm.Sort
import org.schulcloud.mobile.utils.allAsLiveData
import org.schulcloud.mobile.utils.firstAsLiveData

class NewsDao(private val realm: Realm) {

    fun listNews(): LiveData<List<News>> {
        return realm.where(News::class.java)
                .sort("createdAt", Sort.DESCENDING)
                .allAsLiveData()
    }

    fun news(id: String): LiveData<News?> {
        return realm.where(News::class.java)
                .equalTo("id", id)
                .firstAsLiveData()
    }
}
