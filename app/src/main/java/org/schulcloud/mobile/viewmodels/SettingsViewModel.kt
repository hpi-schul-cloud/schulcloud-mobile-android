package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.R
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.base.RealmObjectLiveData
import org.schulcloud.mobile.models.notifications.Device
import org.schulcloud.mobile.models.notifications.DeviceRequest
import org.schulcloud.mobile.models.notifications.NotificationRepository
import org.schulcloud.mobile.models.user.Account
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.storages.UserStorage

class SettingsViewModel: ViewModel(){
    companion object {
        val TAG = SettingsViewModel::class.java.simpleName
    }

    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    val user: LiveData<User?>
        get() = UserRepository.currentUser(realm)
    val account: LiveData<User?>
        get() = UserRepository.currentUser(realm)
    val genderIds = intArrayOf(R.string.gender_not_selected,R.string.gender_male,
    R.string.gender_female, R.string.gender_other)

    suspend fun resyncUser(){
        UserRepository.syncUser(UserStorage().userId!!)
    }

}