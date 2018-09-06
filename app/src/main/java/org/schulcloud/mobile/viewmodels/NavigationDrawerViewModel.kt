package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.models.user.UserRepository

class NavigationDrawerViewModel : ViewModel() {
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val user: LiveData<User?> = UserRepository.currentUser(realm)
}
