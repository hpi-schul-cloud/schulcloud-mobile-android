package org.schulcloud.mobile.models.homework

import android.graphics.Color
import android.support.v4.content.ContextCompat
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.R
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

    @Throws(ParseException::class)
    fun getDueTextAndColorId(): Pair<String, Int> { //may get an exception
        val receivedFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val dueTillCal: Calendar = Calendar.getInstance()
        dueTillCal.time = receivedFormat.parse(dueDate)
        val currentCal: Calendar = Calendar.getInstance()
        val diff: Long = dueTillCal.timeInMillis - currentCal.timeInMillis
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diff)

        when (diffInDays) {
            in Int.MIN_VALUE until 0 -> {
                return Pair("⚐ Überfällig", Color.RED)
            }
            0L -> {
                val diffInHours = TimeUnit.MILLISECONDS.toHours(diff)
                if (diffInHours > 0) {
                    return Pair("⚐ In $diffInHours Stunden fällig", Color.RED)
                } else {
                    return Pair("⚐ Überfällig", Color.RED)
                }
            }
            1L -> return Pair("⚐ Morgen fällig", Color.RED)
            2L -> return Pair("Übermorgen", Color.BLACK)
            in 3L..7L -> return Pair("In $diffInDays Tagen", Color.BLACK)
            else -> {
                return Pair("", Color.WHITE)
            }
        }

    }
}