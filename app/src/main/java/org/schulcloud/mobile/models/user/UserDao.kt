package org.schulcloud.mobile.models.user

import android.arch.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.utils.asLiveData

class UserDao(private val realm: Realm){
    fun currentUser(realm: Realm): LiveData<User?> {
        return realm.where(User::class.java)
                .findFirstAsync()
                .asLiveData()
    }

    fun getAccount(realm: Realm): LiveData<Account?> {
        return realm.where(Account::class.java)
                .findFirstAsync()
                .asLiveData()
    }
}