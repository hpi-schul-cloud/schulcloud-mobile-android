package org.schulcloud.mobile.models

import android.util.Log
import io.realm.Realm
import io.realm.RealmModel
import org.schulcloud.mobile.BuildConfig

abstract class Sync<S: RealmModel>(private val clazz: Class<S>) {

    var handleDeletes: Boolean = true

    companion object {
        val TAG: String = Sync::class.java.simpleName
    }

    fun saveOnly() : Sync<S> {
        handleDeletes = false
        return this
    }

    /**
     *
     */
    class Data<S: RealmModel>(private val clazz: Class<S>, private val items: List<S>) : Sync<S>(clazz) {

        companion object {
            fun <S : RealmModel> with(clazz: Class<S>, items: List<S>): Data<S> {
                return Data(clazz, items)
            }
        }

        fun run() {
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {

                // Copy or Update
                for(item: S in items) {
                    realm.copyToRealmOrUpdate(item)
                }
                if (BuildConfig.DEBUG) Log.d(TAG, "DATA: Saved " + items.size + " data resources from type " + clazz.simpleName)

                /*
                // Handle Delete
                if(handleDeletes) {
                    // if (BuildConfig.DEBUG) Log.d(TAG, "DATA: Deleted " + results.size + " local resources from type " + clazz.simpleName)
                } else if(BuildConfig.DEBUG) Log.d(TAG, "DATA: Deleted 0 local resources from type " + clazz.simpleName)
                */
            }
            realm.close()
        }
    }
}