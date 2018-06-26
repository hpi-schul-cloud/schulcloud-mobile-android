package  org.schulcloud.mobile.models.base

import io.realm.RealmChangeListener
import android.arch.lifecycle.LiveData
import io.realm.RealmObject


class RealmObjectLiveData<T : RealmObject>(private val result: T) : LiveData<T>() {

    private val listener = RealmChangeListener<T> { resuls -> value = result }

    override fun onActive() {
        result.addChangeListener(listener)
    }

    override fun onInactive() {
        result.removeChangeListener(listener)
    }
}