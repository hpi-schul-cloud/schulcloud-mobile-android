package org.schulcloud.mobile.models.event

import android.arch.lifecycle.LiveData
import android.util.Log
import io.realm.Realm
import org.schulcloud.mobile.utils.asLiveData
import org.schulcloud.mobile.utils.map
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
                .map({ events ->
                    val weekdayCurrent = GregorianCalendar().get(Calendar.DAY_OF_WEEK)
                    Log.d("Weekday", Integer.toString(weekdayCurrent))

                    val c = Calendar.getInstance()
                    events.filter {
                        // filter for today
                        it.included?.any {
                            val attributes = it.attributes ?: return@any false

                            val freq = attributes.freq ?: return@any false
                            val weekday = attributes.weekday ?: return@any false
                            freq == IncludedAttributes.FREQ_DAILY
                                    || (freq == IncludedAttributes.FREQ_WEEKLY && getNumberForWeekday(weekday) == weekdayCurrent)
                        } ?: false
                    }.sortedBy {
                        // sort ascending by start
                        it.start?.let { start ->
                            c.timeInMillis = start
                            (c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE)) * 60 + c.get(Calendar.SECOND)
                        }
                    }.toList()
                })
    }

    private fun getNumberForWeekday(weekday: String): Int? {
        return when (weekday) {
            "SU" -> 1
            "MO" -> 2
            "TU" -> 3
            "WE" -> 4
            "TH" -> 5
            "FR" -> 6
            "SA" -> 7
            else -> null
        }
    }

    fun event(id: String): LiveData<Event?> {
        return realm.where(Event::class.java)
                .equalTo("id", id)
                .findFirstAsync()
                .asLiveData()
    }
}
