package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.google.android.gms.common.UserRecoverableException
import io.realm.Realm
import org.schulcloud.mobile.models.base.RealmObjectLiveData
import org.schulcloud.mobile.models.user.Account
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.utils.map

class UserSettingsViewModel: ViewModel(){
    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    val user = UserRepository.currentUser(realm)
    val account = UserRepository.getAccount(realm)

    suspend fun patchUser(user: User){
        UserRepository.patchUser(user)
    }

    suspend fun patchAccount(account: Account){
        UserRepository.patchAccount(account)
    }
}