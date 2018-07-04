package org.schulcloud.mobile.models.homework

import android.graphics.Color
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.joda.time.*
import org.joda.time.format.DateTimeFormat

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

    /**
     * Returns text and text color for the duetill label of a Homework object
     * depending on how many days and hours are left until its dueDate.
     *
     * When there is more than a week left or the Homework object does not have a dueDate
     * or the dueDate cannot be parsed, this function returns an empty String
     *
     * @return a Pair with duetill label text and color
     */
    fun getDueTextAndColorId(): Pair<String, Int> {
        val diffDays = getDueTimespanDays()

        when (diffDays) {
            in Int.MIN_VALUE + 1 until 0 -> {
                return Pair("Überfällig", Color.RED)
            }
            0 -> {
                val diffHours = getDueTimespanHours()
                if (diffHours >= 0) {
                    return Pair("In $diffHours Stunden fällig", Color.RED)
                } else if (diffHours == Int.MIN_VALUE) {
                    return Pair("", Color.TRANSPARENT)
                } else {
                    return Pair("Überfällig", Color.RED)
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
     * In case of no dueDate given or parsing error this function returns minimal Int value
     *
     * @return the number of days until deadline
     */
    fun getDueTimespanDays(): Int {
        var dueTimespanDays: Int = Int.MIN_VALUE
        try {
            dueTimespanDays = Days.daysBetween(LocalDate.now(), getDueTillDateTime().toLocalDate()).days
        } catch (e: Exception) {
        }
        return dueTimespanDays
    }

    private fun getDueTimespanHours(): Int {
        var dueTimespanHours: Int = Int.MIN_VALUE
        try {
            dueTimespanHours = Hours.hoursBetween(LocalDateTime.now(), getDueTillDateTime().toLocalDateTime()).hours
        } catch (e: Exception) {
        }
        return dueTimespanHours

    }

    @Throws(Exception::class)
    fun getDueTillDateTime(): DateTime {
        return DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parseDateTime(dueDate!!)
    }
}