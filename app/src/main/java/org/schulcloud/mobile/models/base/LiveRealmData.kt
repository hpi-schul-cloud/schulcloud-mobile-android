package org.schulcloud.mobile.models.base

import io.realm.RealmResults
import io.realm.RealmChangeListener
import android.arch.lifecycle.LiveData
import io.realm.RealmModel


class LiveRealmData<T : RealmModel>(private val results: RealmResults<T>) : LiveData<RealmResults<T>>() {

    private val listener = RealmChangeListener<RealmResults<T>> { results -> value = results }

    override fun onActive() {
        results.addChangeListener(listener)
    }

    override fun onInactive() {
        results.removeChangeListener(listener)
    }
}