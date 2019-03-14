package org.schulcloud.mobile

import io.realm.RealmObject
import org.schulcloud.mobile.models.base.HasId

class TestIdRealmObject : RealmObject(), HasId {
    override var id: String = ""
}
