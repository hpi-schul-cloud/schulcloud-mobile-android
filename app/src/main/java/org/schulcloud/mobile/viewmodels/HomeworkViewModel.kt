package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.realm.Realm
import io.realm.RealmResults
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.homework.HomeworkRepository

class HomeworkViewModel () : ViewModel() {
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    private lateinit var homework: LiveData<RealmResults<Homework>>

    fun getHomework(): LiveData<RealmResults<Homework>> {
        return homework
    }

    fun setHomeworkForId(id: String){
       homework = HomeworkRepository.getHomeworkForId(realm, id)
    }
}