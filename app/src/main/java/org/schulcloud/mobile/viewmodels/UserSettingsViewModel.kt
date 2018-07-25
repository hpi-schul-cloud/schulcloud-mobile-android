package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.ViewModel
import com.google.android.gms.common.UserRecoverableException
import io.realm.Realm
import org.schulcloud.mobile.models.base.RealmObjectLiveData
import org.schulcloud.mobile.models.user.Account
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.models.user.UserRepository

class UserSettingsViewModel: ViewModel(){
    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    private val _user = UserRepository.currentUser(realm)
    private val _account = UserRepository.getAccount(realm)

    val user: RealmObjectLiveData<User>
        get() = _user

    val account: RealmObjectLiveData<Account>
        get() = _account

    suspend fun patchUser(user: User){
        UserRepository.patchUser(user)
    }

    suspend fun patchAccount(account: Account){
        UserRepository.patchAccount(account)
    }
}