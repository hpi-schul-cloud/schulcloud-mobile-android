package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.base.RealmObjectLiveData
import org.schulcloud.mobile.models.notifications.Device
import org.schulcloud.mobile.models.notifications.DeviceRequest
import org.schulcloud.mobile.models.notifications.NotificationRepository
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.models.user.UserRepository

class SettingsViewModel: ViewModel(){
    companion object {
        val TAG = SettingsViewModel::class.java.simpleName
    }

    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    private val _user = UserRepository.currentUser(realm)

    val user: LiveData<User?>
        get() = _user
}