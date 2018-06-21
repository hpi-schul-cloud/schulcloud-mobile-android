package org.schulcloud.mobile.models.homework

import android.graphics.Color
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

open class Homework : RealmObject() {

    @PrimaryKey
    @SerializedName("_id")
    var id: String = ""

    var schoold: String? = null
    @SerializedName("name")
    var title: String? = null
    var courseId: HomeworkCourse? = null
    var dueDate: String? = null
    var description: String? = null
    @SerializedName("private")
    var restricted: Boolean = false

    fun getDueTextAndColorId(): Pair<String, Int> {
        val diffMillis = getDueTimespanMillis()
        val diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis)

        when (diffDays) {
            in Long.MIN_VALUE + 1 until 0 -> {
                return Pair("⚐ Überfällig", Color.RED)
            }
            0L -> {
                val diffInHours = TimeUnit.MILLISECONDS.toHours(diffMillis)
                if (diffInHours > 0) {
                    return Pair("⚐ In $diffInHours Stunden fällig", Color.RED)
                } else {
                    return Pair("⚐ Überfällig", Color.RED)
                }
            }
            1L -> return Pair("⚐ Morgen fällig", Color.RED)
            2L -> return Pair("Übermorgen", Color.BLACK)
            in 3L..7L -> return Pair("In $diffDays Tagen", Color.BLACK)
            else -> {
                return Pair("", Color.WHITE)
            }
        }

    }

    fun getDueTimespanMillis(): Long {
        val dueTillCal: Calendar = Calendar.getInstance()
        val currentCal: Calendar = Calendar.getInstance()
        var timespan: Long = Long.MIN_VALUE
        try {
            dueTillCal.time = getDueTillDate()
            timespan = dueTillCal.timeInMillis - currentCal.timeInMillis
        } catch (e: Exception) {
        }
        return timespan
    }

    @Throws(Exception::class)
    fun getDueTillDate(): Date {
        val receivedFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return receivedFormat.parse(dueDate)
    }
}