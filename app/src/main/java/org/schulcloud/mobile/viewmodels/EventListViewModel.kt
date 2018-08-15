package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.event.Event
import org.schulcloud.mobile.models.event.EventRepository

class EventListViewModel : ViewModel() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val events: LiveData<List<Event>> = EventRepository.eventsForToday(realm)
}
