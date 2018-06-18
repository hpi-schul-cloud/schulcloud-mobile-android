package org.schulcloud.mobile.models.base

import android.arch.lifecycle.LiveData
import io.realm.RealmChangeListener
import io.realm.RealmObject

/**
 * Date: 6/9/2018
 */
class RealmObjectLiveData<T : RealmObject>(private val result: T) : LiveData<T>() {

    private val listener = RealmChangeListener<T> { result ->
        if (result.isValid)
            value = result
    }

    override fun onActive() {
        result.addChangeListener(listener)
    }

    override fun onInactive() {
        result.removeChangeListener(listener)
    }
}
