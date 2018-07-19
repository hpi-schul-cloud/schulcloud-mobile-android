package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.homework.HomeworkRepository

class HomeworkViewModel(id: String) : ViewModel() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    private val _homework: LiveData<Homework?> = HomeworkRepository.homework(realm, id)

    val homework: LiveData<Homework?>
        get() = homework
}
