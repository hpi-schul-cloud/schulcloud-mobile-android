package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.google.android.gms.common.UserRecoverableException
import io.realm.Realm
import org.schulcloud.mobile.R
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.base.RealmObjectLiveData
import org.schulcloud.mobile.models.user.Account
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.utils.map

class UserSettingsViewModel: ViewModel(){
    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    val user: LiveData<User?>
        get() = UserRepository.currentUser(realm)
    val account = UserRepository.getAccount(realm)
    val genderIds = intArrayOf(R.string.gender_not_selected, R.string.gender_male,
            R.string.gender_female, R.string.gender_other)

    suspend fun patchUser(user: User,callback: RequestJobCallback){
        UserRepository.patchUser(user,callback)
    }

    fun checkPassword(email: String,password: String,callback: RequestJobCallback){
        UserRepository.login(email,password,callback)
    }

    suspend fun patchAccount(account: Account, callback: RequestJobCallback){
        if(account.newPassword != "")
            UserRepository.patchAccount(account,callback)
        else
            callback.success()
    }
}