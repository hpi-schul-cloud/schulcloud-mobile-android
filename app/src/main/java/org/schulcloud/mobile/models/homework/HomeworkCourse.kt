package org.schulcloud.mobile.models.homework

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.models.base.RealmString


open class HomeworkCourse : RealmObject() {

    @PrimaryKey
    @SerializedName("_id")
    var id: String = ""

    var schoolId: String? = null
    var name: String? = null
    var color: String? = null

    var substitutionIds: RealmList<RealmString>? = null
}
