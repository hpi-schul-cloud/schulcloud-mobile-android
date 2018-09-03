package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.news.News
import org.schulcloud.mobile.models.news.NewsRepository

class NewsListViewModel : ViewModel() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val news: LiveData<List<News>> = NewsRepository.news(realm)
}
