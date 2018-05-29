package org.schulcloud.mobile.models.news

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class News : RealmObject(){
    //TODO: add serialized name here
    @PrimaryKey
    var id: String = ""

    var schoolId: String? = null
    var title: String? = null
    var createdAt: String? = null
    var content: String? = null


}