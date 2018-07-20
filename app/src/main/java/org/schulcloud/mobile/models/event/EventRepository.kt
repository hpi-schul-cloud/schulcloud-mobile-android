package org.schulcloud.mobile.models.event

import android.arch.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.jobs.ListEventsJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.utils.eventDao

object EventRepository {

    fun events(realm: Realm): LiveData<List<Event>> {
        return realm.eventDao().events()
    }

    fun eventsForMonth(realm: Realm, year: Int, month: Int): LiveData<Sequence<Event>> {
        return realm.eventDao().eventsForMonth(year, month)
    }

    fun eventsForToday(realm: Realm): LiveData<List<Event>> {
        return realm.eventDao().eventsForToday()
    }

    fun event(realm: Realm, id: String): LiveData<Event?> {
        return realm.eventDao().event(id)
    }

    suspend fun syncEvents() {
        ListEventsJob(object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code: ErrorCode) {
            }
        }).run()
    }
}
