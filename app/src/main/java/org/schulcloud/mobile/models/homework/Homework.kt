package org.schulcloud.mobile.models.homework

import android.graphics.Color
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.joda.time.*
import org.joda.time.format.DateTimeFormat
import org.schulcloud.mobile.utils.HOST

open class Homework : RealmObject() {

    @PrimaryKey
    @SerializedName("_id")
    var id: String = ""

    var schoold: String? = null
    @SerializedName("name")
    var title: String? = null
    var description: String? = null
    var dueDate: String? = null
    @SerializedName("private")
    var restricted: Boolean = false
    var courseId: HomeworkCourse? = null

    val url: String
        get() = "${HOST}/homework/$id"

    /**
     * Returns text and text color for the duetill label of a Homework object
     * depending on how many days and hours are left until its dueDate.
     *
     * When there is more than a week left or the dueDate cannot be parsed, this function returns an empty String
     *
     * @return a Pair with duetill label text and color
     */
    fun getDueTextAndColorId(): Pair<String, Int> {
        val diffDays = getDueTimespanDays()

        when (diffDays) {
            in Int.MIN_VALUE until 0 -> {
                return Pair("Überfällig", Color.RED)
            }
            0 -> {
                val diffHours = getDueTimespanHours()
                when (diffHours) {
                    in 0 until Int.MAX_VALUE -> {
                        return Pair("In $diffHours Stunden fällig", Color.RED)
                    }
                    Int.MAX_VALUE -> {
                        return Pair("", Color.TRANSPARENT)
                    }
                    else -> {
                        return Pair("Überfällig", Color.RED)
                    }
                }
            }
            1 -> return Pair("Morgen fällig", Color.RED)
            2 -> return Pair("Übermorgen", Color.BLACK)
            in 3..7 -> return Pair("In $diffDays Tagen", Color.BLACK)
            else -> {
                return Pair("", Color.TRANSPARENT)
            }
        }
    }

    /**
     * Calculates how many days are left until the dueDate.
     * When the given date string cannot be parsed, this function returns maximal Int value
     *
     * @return the number of days until deadline
     */
    fun getDueTimespanDays(): Int {
        return try {
            Days.daysBetween(LocalDateTime.now(), getDueTillDateTime().toLocalDateTime()).days
        } catch (e: IllegalArgumentException) {
            Int.MAX_VALUE
        }
    }

    private fun getDueTimespanHours(): Int {
        return try {
            Hours.hoursBetween(LocalDateTime.now(), getDueTillDateTime().toLocalDateTime()).hours
        } catch (e: IllegalArgumentException) {
            Int.MAX_VALUE
        }
    }


    @Throws(IllegalArgumentException::class)
    fun getDueTillDateTime(): DateTime {
        return DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parseDateTime(dueDate)
    }
}
