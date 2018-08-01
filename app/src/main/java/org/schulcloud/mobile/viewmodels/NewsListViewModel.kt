package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.realm.Realm
import io.realm.RealmResults
import org.schulcloud.mobile.models.news.News
import org.schulcloud.mobile.models.news.NewsRepository

class NewsListViewModel : ViewModel() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    private val _news: LiveData<RealmResults<News>?> = NewsRepository.news(realm)

    val news: LiveData<RealmResults<News>?>
        get() = _news
}
