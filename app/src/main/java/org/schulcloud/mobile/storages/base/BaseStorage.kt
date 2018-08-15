package org.schulcloud.mobile.storages.base

import android.content.SharedPreferences
import org.schulcloud.mobile.SchulCloudApp

abstract class BaseStorage(name: String, mode: Int) {

    private val preferences: SharedPreferences = SchulCloudApp.instance.getSharedPreferences(name, mode)

    fun getString(key: String, defValue: String? = null): String? = preferences.getString(key, defValue)

    fun putString(key: String, value: String?) {
        preferences
                .edit()
                .putString(key, value)
                .apply()
    }

    fun delete() {
        preferences
                .edit()
                .clear()
                .apply()
    }
}
