package org.schulcloud.mobile.models.event

import androidx.lifecycle.LiveData
import android.util.Log
import io.realm.Realm
import org.schulcloud.mobile.utils.asLiveData
import org.schulcloud.mobile.utils.isToday
import org.schulcloud.mobile.utils.map
import org.schulcloud.mobile.utils.timeOfDay
import java.util.*

class EventDao(private val realm: Realm) {

    fun events(): LiveData<List<Event>> {
        return realm.where(Event::class.java)
                .findAllAsync()
                .asLiveData()
    }

    fun eventsForToday(): LiveData<List<Event>> {
        return realm.where(Event::class.java)
                .findAllAsync()
                .asLiveData()
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
                .findFirstAsync()
                .asLiveData()
    }
}
