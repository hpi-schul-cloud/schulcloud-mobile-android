package org.schulcloud.mobile.models.homework

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Hours
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.schulcloud.mobile.models.base.HasId

open class Homework : RealmObject(), HasId {

    @PrimaryKey
    @SerializedName("_id")
    override var id: String = ""

    var teacherId: String? = null
    @SerializedName("name")
    var title: String? = null
    var description: String? = null
    var dueDate: String? = null
    @SerializedName("courseId")
    var course: HomeworkCourse? = null

    @SerializedName("private")
    var restricted: Boolean = false
    var publicSubmissions: Boolean = false

    val dueDateTime: DateTime?
        get() = try {
            DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parseDateTime(dueDate)
        } catch (e: IllegalArgumentException) {
            null
        }

    val dueTimespanDays: Int?
        get() = dueDateTime?.let {
            Days.daysBetween(LocalDateTime.now(), it.toLocalDateTime()).days
        }

    val dueTimespanHours: Int?
        get() = dueDateTime?.let {
            Hours.hoursBetween(LocalDateTime.now(), it.toLocalDateTime()).hours
        }


    fun isTeacher(userId: String): Boolean {
        return teacherId == userId
                || course?.substitutionIds?.any { it.value == userId } == true
    }
}
