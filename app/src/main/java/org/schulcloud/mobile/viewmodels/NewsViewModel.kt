package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.news.News
import org.schulcloud.mobile.models.news.NewsRepository

class NewsViewModel(val id: String) : ViewModel() {
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val news: LiveData<News?> = NewsRepository.news(realm, id)
}
