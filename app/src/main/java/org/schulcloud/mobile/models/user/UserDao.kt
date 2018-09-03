package org.schulcloud.mobile.models.user

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.utils.firstAsLiveData

class UserDao(private val realm: Realm) {
    fun user(id: String): LiveData<User?> {
        return realm.where(User::class.java)
                .equalTo("id", id)
                .firstAsLiveData()
    }
}
