package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.event.Event

class CalendarViewModel : ViewModel() {
    val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val events: MutableLiveData<MutableMap<Pair<Int, Int>, Sequence<Event>>> = MutableLiveData()

    val eventIds: MutableList<Event> = mutableListOf()
    fun getIdForEvent(event: Event): Long {
        val id = eventIds.indexOf(event)
        return if (id >= 0)
            id.toLong()
        else {
            eventIds.add(event)
            eventIds.size.toLong()
        }
    }
}
