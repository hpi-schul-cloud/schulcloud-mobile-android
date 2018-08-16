package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.homework.HomeworkRepository

class HomeworkViewModel(id: String) : ViewModel() {
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val homework: LiveData<Homework?> = HomeworkRepository.homework(realm, id)
}
