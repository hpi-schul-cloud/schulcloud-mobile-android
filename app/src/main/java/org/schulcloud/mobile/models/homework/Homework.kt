package org.schulcloud.mobile.models.homework

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

    @SerializedName("name")
    var title: String? = null
    var description: String? = null
    var dueDate: String? = null
    @SerializedName("private")
    var restricted: Boolean = false
    var courseId: HomeworkCourse? = null

    val url: String
        get() = "${HOST}/homework/$id"

    val dueDateTime: DateTime?
        get() {
            return try {
                DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parseDateTime(dueDate)
            } catch (e: IllegalArgumentException) {
                null
            }
        }

    /**
     * returns Int.MAX_VALUE when the dueDate String is invalid and cannot be parsed to DateTime
     */
    val dueTimespanDays: Int
        get() {
            dueDateTime?.let {
                return Days.daysBetween(LocalDateTime.now(), it.toLocalDateTime()).days
            }
            return Int.MAX_VALUE
        }

    /**
     * returns Int.MAX_VALUE when the dueDate String is invalid and cannot be parsed to DateTime
     */
    val dueTimespanHours: Int
        get() {
            dueDateTime?.let {
                return Hours.hoursBetween(LocalDateTime.now(), it.toLocalDateTime()).hours
            }
            return Int.MAX_VALUE
        }
}
