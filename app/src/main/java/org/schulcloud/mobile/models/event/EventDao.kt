package org.schulcloud.mobile.models.event

import android.util.Log
import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.utils.*
import java.util.*

class EventDao(private val realm: Realm) {

    fun events(): LiveData<List<Event>> {
        return realm.where(Event::class.java)
                .allAsLiveData()
    }

    fun eventsForToday(): LiveData<List<Event>> {
        return realm.where(Event::class.java)
                .allAsLiveData()
                .map { events ->
                    val weekdayCurrent = GregorianCalendar().get(Calendar.DAY_OF_WEEK)
                    Log.d("Weekday", Integer.toString(weekdayCurrent))

                    val c = Calendar.getInstance()
                    events.filter {
                        it.nextStart(true)?.isToday ?: false
                    }.sortedBy {
                        // sort ascending by start
                        it.start?.let { start ->
                            c.timeInMillis = start
                            c.timeOfDay
                        }
                    }.toList()
                }
    }

    fun event(id: String): LiveData<Event?> {
        return realm.where(Event::class.java)
                .equalTo("id", id)
                .firstAsLiveData()
    }
}
