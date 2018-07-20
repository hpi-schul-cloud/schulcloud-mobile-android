package org.schulcloud.mobile.storages

import android.content.Context
import org.schulcloud.mobile.storages.base.BaseStorage

class UserStorage : BaseStorage(PREF_USER, Context.MODE_PRIVATE) {

    companion object {
        private const val PREF_USER = "pref_user_v2"

        private const val USER_ID = "id"
        private const val ACCESS_TOKEN = "token"
        private const val USER_FIRSTNAME = "firstname"
        private const val USER_LASTNAME = "lastname"
        private const val USER_EMAIL = "email"
    }

    var userId: String?
        get() = getString(USER_ID)
        set(value) = putString(USER_ID, value)

    var accessToken: String?
        get() = getString(ACCESS_TOKEN)
        set(value) = putString(ACCESS_TOKEN, value)

    var firstname: String?
        get() = getString(USER_FIRSTNAME)
        set(value) = putString(USER_FIRSTNAME,value)

    var lastname: String?
        get() = getString(USER_LASTNAME)
        set(value) = putString(USER_LASTNAME,value)

    var email: String?
        get() = getString(USER_EMAIL)
        set(value) = putString(USER_EMAIL,value)
}
