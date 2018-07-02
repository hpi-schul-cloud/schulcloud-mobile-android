package org.schulcloud.mobile.data.local;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmModel;

@Singleton
public class BaseDatabaseHelper {
    protected final Provider<Realm> mRealmProvider;

    @Inject
    BaseDatabaseHelper(Provider<Realm> realmProvider) {
        mRealmProvider = realmProvider;
    }

    public void clearTable(@NonNull Class<? extends RealmModel> table) {
        mRealmProvider.get().executeTransaction(realm -> realm.delete(table));
    }
    public void clearAll() {
        mRealmProvider.get().executeTransaction(realm -> realm.deleteAll());
    }
}
