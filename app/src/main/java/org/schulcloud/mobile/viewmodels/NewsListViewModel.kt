package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.news.News
import org.schulcloud.mobile.models.news.NewsRepository

class NewsListViewModel : ViewModel() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val news: LiveData<List<News>> = NewsRepository.news(realm)
}
