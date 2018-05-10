package org.schulcloud.mobile.data.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.schulcloud.mobile.data.model.AccessToken;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.User;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Single;
import timber.log.Timber;

@Singleton
public class UserDatabaseHelper extends BaseDatabaseHelper {

    @Inject
    UserDatabaseHelper(Provider<Realm> realmProvider) {
        super(realmProvider);
    }

    public Observable<User> setUsers(final Collection<User> newUsers) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed())
                return;
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

    @NonNull
    public Observable<List<User>> getUsers() {
        final Realm realm = mRealmProvider.get();
        return realm.where(User.class).findAllAsync().asObservable()
                .filter(RealmResults::isLoaded)
                .map(realm::copyFromRealm);
    }
    @Nullable
    public User getUser(@Nullable String id) {
        if (id == null)
            return null;
        return mRealmProvider.get().where(User.class).equalTo("_id", id).findFirst();
    }

    public Observable<AccessToken> setAccessToken(final AccessToken newAccessToken) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed())
                return;
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
        return Observable.just(realm.where(AccessToken.class).findFirstAsync());
    }

    public Observable<CurrentUser> setCurrentUser(final CurrentUser currentUser) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed())
                return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(currentUser));
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

    @NonNull
    public Single<CurrentUser> getCurrentUser() {
        final Realm realm = mRealmProvider.get();
        return Single.just(realm.where(CurrentUser.class).findFirst());
    }
}
