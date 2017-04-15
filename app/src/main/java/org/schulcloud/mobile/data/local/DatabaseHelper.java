package org.schulcloud.mobile.data.local;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.schulcloud.mobile.data.model.AccessToken;
import org.schulcloud.mobile.data.model.User;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import timber.log.Timber;

@Singleton
public class DatabaseHelper {

    private final Provider<Realm> mRealmProvider;

    @Inject
    DatabaseHelper(Provider<Realm> realmProvider) {
        mRealmProvider = realmProvider;
    }

    public Observable<User> setUsers(final Collection<User> newUsers) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(newUsers));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        });
    }

    public Observable<List<User>> getUsers() {
        final Realm realm = mRealmProvider.get();
        return realm.where(User.class).findAllAsync().asObservable()
                .filter(users -> users.isLoaded())
                .map(users -> realm.copyFromRealm(users));
    }

    public Observable<AccessToken> setAccessToken(final AccessToken newAccessToken) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(newAccessToken));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    subscriber.onCompleted();
                    realm.close();
                }
            }
        });
    }

    public Observable<AccessToken> getAccessToken() {
        final Realm realm = mRealmProvider.get();
        return realm.where(AccessToken.class).findFirstAsync().asObservable();
    }

    /*public Observable<List<User>> getUsers() {
        final Realm realm = mRealmProvider.get();
        RealmResults<User> realmUsers = realm.where(User.class).findAll();

        realm.beginTransaction();
        List<User> users = realm.copyFromRealm(realmUsers);
        realm.commitTransaction();

        realm.close();

        return Observable.just(users);
    }*/
}
