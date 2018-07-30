package org.schulcloud.mobile.models.homework

import android.arch.lifecycle.LiveData
import io.realm.Realm
import io.realm.Sort
import org.schulcloud.mobile.utils.asLiveData
import org.schulcloud.mobile.utils.map

class HomeworkDao(private val realm: Realm) {

    fun homeworkList(): LiveData<List<Homework>> {
        return realm.where(Homework::class.java)
                .findAllAsync()
                .asLiveData()
                .map { homeworkList ->
                    homeworkList.let {
                        val outdatedHomework = homeworkList.filter { it.dueTimespanDays < 0 }
                                .sortedByDescending { it.dueDate }
                                .toList()

                        val displayedHomework = homeworkList.filter { it.dueTimespanDays >= 0 }
                                .sortedBy { it.dueDate }
                                .toMutableList()

                        displayedHomework.addAll(outdatedHomework)

                        displayedHomework
                    }
                }
    }

    fun homework(id: String): LiveData<Homework?> {
        return realm.where(Homework::class.java)
                .sort("dueDate", Sort.ASCENDING)
                .equalTo("id", id)
                .findFirstAsync()
                .asLiveData()
    }
}
