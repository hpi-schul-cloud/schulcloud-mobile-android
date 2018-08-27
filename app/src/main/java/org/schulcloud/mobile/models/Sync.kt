package org.schulcloud.mobile.models

import android.util.Log
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmQuery
import io.realm.kotlin.deleteFromRealm
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.models.base.HasId


abstract class Sync<T> where T : RealmModel, T : HasId {
    companion object {
        val TAG: String = Sync::class.java.simpleName
    }

    class Data<T>(
        private val clazz: Class<T>,
        private val items: List<T>,
        private val toDelete: (RealmQuery<T>.() -> RealmQuery<T>)?
    ) : Sync<T>() where T : RealmModel, T : HasId {
        companion object {
            fun <T> with(
                clazz: Class<T>,
                items: List<T>,
                toDelete: (RealmQuery<T>.() -> RealmQuery<T>)? = null
            ): Data<T> where T : RealmModel, T : HasId {
                return Data(clazz, items, toDelete)
            }
        }

        fun run() {
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                // Copy or Update
                for (item in items)
                    realm.copyToRealmOrUpdate(item)
                if (BuildConfig.DEBUG) Log.d(TAG, "Saved ${items.size} ${clazz.simpleName}s")

                // Remove old items
                val oldItems = toDelete?.invoke(realm.where(clazz))?.findAll()
                        ?.filter { oldItem -> !items.any { oldItem.id == it.id } }
                for (oldItem in oldItems ?: emptyList<T>())
                    oldItem.deleteFromRealm()
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "Deleted ${oldItems?.size} local ${clazz.simpleName}s")
            }
            realm.close()
        }
    }

    class SingleData<T>(
        private val clazz: Class<T>,
        private val item: T?,
        private val toDelete: (Realm.() -> RealmQuery<T>)?
    ) : Sync<T>() where T : RealmModel, T : HasId {
        companion object {
            fun <T> with(
                clazz: Class<T>,
                item: T?,
                id: String? = null
            ): SingleData<T> where T : RealmModel, T : HasId {
                return SingleData(clazz, item, { realm: Realm ->
                    realm.where(clazz)
                            .equalTo("id", id)
                }.takeIf { id != null })
            }
        }

        fun run() {
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                // Copy or Update
                if (item != null) {
                    realm.copyToRealmOrUpdate(item)
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "Saved 1 item (${item.id}) from type ${clazz.simpleName}")
                }

                // Remove old items
                val oldItems = toDelete?.invoke(realm)?.findAll()
                        ?.filter { it.id != item?.id }
                for (oldItem in oldItems ?: emptyList<T>())
                    oldItem.deleteFromRealm()
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "Deleted ${oldItems?.size} local resources from type ${clazz.simpleName}")
            }
            realm.close()
        }
    }
}
