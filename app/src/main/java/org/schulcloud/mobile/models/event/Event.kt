package org.schulcloud.mobile.models.event

import com.google.gson.annotations.SerializedName
import com.jonaswanke.calendar.Week
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.utils.*
import java.util.*

open class Event : RealmObject() {
    companion object {
        const val TYPE_TEMPLATE = "template"
    }

    @PrimaryKey
    @SerializedName("_id")
    var id: String? = null
    var type: String? = null
    var title: String? = null
    var allDay: Boolean? = null
    var start: Long? = null
    var end: Long? = null
    var summary: String? = null
    var location: String? = null
    var description: String? = null
    var included: RealmList<Included>? = null

    @SerializedName("x-sc-courseId")
    var courseId: String? = null


    val duration: Long?
        get() {
            val start = start ?: return null
            val end = end ?: return null
            return end - start
        }

    fun nextStart(includeCurrent: Boolean = false): Calendar? {
        val start = start ?: return null
        val end = end ?: return null
        val startOrEnd = if (includeCurrent) end else start

        val current = getCalendar().apply { fromLocal() }
        val currentTimeOfDay = current.timeOfDay

        // Test all repetition definitions
        return included.orEmpty().map {
            val attributes = it.attributes ?: return@map null
            val until = attributes.until?.parseDate()
            if (until != null && until.timeInMillis < current.timeInMillis)
                return@map null

            val freq = attributes.freq ?: return@map null
            val weekdayNumber = attributes.weekdayNumber ?: return@map null
            val timeOfDay = getUserCalendar().apply {
                timeInMillis = startOrEnd
                toLocal()
            }.timeOfDay
            val startTimeOfDay = getUserCalendar().apply {
                timeInMillis = start
                toLocal()
            }.timeOfDay

            val cal = getCalendar()
            // First repeated event now or later
            when (freq) {
                IncludedAttributes.FREQ_DAILY ->
                    if (timeOfDay >= currentTimeOfDay)
                        cal.apply { this.timeOfDay = startTimeOfDay }
                    else
                        cal.apply {
                            this.timeOfDay = startTimeOfDay
                            add(Calendar.DAY_OF_MONTH, 1)
                        }

                IncludedAttributes.FREQ_WEEKLY ->
                    if (weekdayNumber > current.dayOfWeek
                            || (weekdayNumber == current.dayOfWeek && timeOfDay >= currentTimeOfDay))
                        cal.apply {
                            dayOfWeek = weekdayNumber
                            this.timeOfDay = startTimeOfDay
                        }
                    else
                        cal.apply {
                            add(Calendar.WEEK_OF_MONTH, 1)
                            dayOfWeek = weekdayNumber
                            this.timeOfDay = startTimeOfDay
                        }

                else -> null
            }
        }
                .let {
                    val startCal = getUserCalendar().apply { timeInMillis = start }
                    // If repetition only starts now or later, take the first instance
                    if (start >= current.timeInMillis)
                        listOf(startCal)
                    else if (end >= current.timeInMillis)
                        it.toMutableList()
                                .apply { add(startCal) }
                    else
                        it
                }
                .filterNotNull()
                // from all that apply, take the first occurrence
                .minBy { it.timeInMillis }
    }

    fun getOccurrencesForWeek(week: Week): Sequence<Event> {
        val cal = getCalendar()

        val start = start ?: return emptySequence()
        if (start >= week.end)
            return emptySequence()
        val duration = duration ?: return emptySequence()


        // Test all repetition definitions
        var repetitions = included.orEmpty().asSequence().flatMap<Included, Long> {
            val attributes = it.attributes ?: return@flatMap emptySequence()
            val until = attributes.until?.parseDate()
            if (until != null && until.timeInMillis < week.start)
                return@flatMap emptySequence()
            val weekdayNumber = attributes.weekdayNumber ?: return@flatMap emptySequence()

            val freq = attributes.freq ?: return@flatMap emptySequence()

            when (freq) {
                IncludedAttributes.FREQ_DAILY -> {
                    // calculate first occurrence in week
                    cal.apply {
                        timeInMillis = start
                        set(Calendar.YEAR, week.year)
                        set(Calendar.WEEK_OF_YEAR, week.week)
                    }
                    generateSequence(cal.timeInMillis) { lastStart ->
                        cal.apply {
                            timeInMillis = lastStart
                            add(Calendar.DATE, 1)
                        }

                        if (cal.timeInMillis < week.end
                                && cal.timeInMillis < until?.timeInMillis ?: Long.MAX_VALUE)
                            cal.timeInMillis
                        else
                            null
                    }
                }

                IncludedAttributes.FREQ_WEEKLY -> {
                    cal.apply {
                        timeInMillis = start
                        set(Calendar.YEAR, week.year)
                        set(Calendar.WEEK_OF_YEAR, week.week)
                        set(Calendar.DAY_OF_WEEK, weekdayNumber)
                    }
                    sequenceOf(cal.timeInMillis)
                }

                else -> emptySequence()
            }
        }
        if (start in week.start until week.end)
            repetitions += sequenceOf(start)

        return repetitions.map { repStart ->
            Event().also {
                it.id = id
                it.type = type
                it.title = title
                it.allDay = allDay
                it.start = repStart
                it.end = repStart + duration
                it.summary = summary
                it.location = location
                it.description = description
                it.included = included
                it.courseId = courseId
            }
        }
    }
}
