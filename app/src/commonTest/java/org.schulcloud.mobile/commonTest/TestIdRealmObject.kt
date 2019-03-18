package org.schulcloud.mobile.commonTest

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.schulcloud.mobile.models.base.HasId

open class TestIdRealmObject : RealmObject(), HasId {
    @PrimaryKey
    override var id: String = ""
}
