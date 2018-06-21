package org.schulcloud.mobile.models.homework

import android.graphics.Color
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.joda.time.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
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
        val diffDays = getDueTimespanDays()

        when (diffDays) {
            in Long.MIN_VALUE + 1 until 0 -> {
                return Pair("⚐ Überfällig", Color.RED)
            }
            0 -> {
                val diffHours = getDueTimespanHours()
                if (diffHours > 0) {
                    return Pair("⚐ In $diffHours Stunden fällig", Color.RED)
                } else {
                    return Pair("⚐ Überfällig", Color.RED)
                }
            }
            1 -> return Pair("⚐ Morgen fällig", Color.RED)
            2 -> return Pair("Übermorgen", Color.BLACK)
            in 3..7 -> return Pair("In $diffDays Tagen", Color.BLACK)
            else -> {
                return Pair("", Color.WHITE)
            }
        }

    }

    fun getDueTimespanDays(): Int {
        val currentDate = LocalDate()
        var dueTimespanDays: Int = Int.MIN_VALUE
        try {
            val dueTillDate = getDueTillDateTime()
            dueTimespanDays = Days.daysBetween(currentDate, dueTillDate.toLocalDate()).days
        } catch (e: Exception) {
        }
        return dueTimespanDays
    }

    fun getDueTimespanHours(): Int {
        val currentDateTime = LocalDateTime()
        var dueTimespanHours: Int = Int.MAX_VALUE
        try {
            val dueTillDateTime = getDueTillDateTime()
            dueTimespanHours = Hours.hoursBetween(currentDateTime, dueTillDateTime.toLocalDateTime()).hours
        } catch (e: Exception) {
        }
        return dueTimespanHours

    }

    @Throws(Exception::class)
    fun getDueTillDateTime(): DateTime {
        val receivedFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return receivedFormat.parseDateTime(dueDate)
    }
}