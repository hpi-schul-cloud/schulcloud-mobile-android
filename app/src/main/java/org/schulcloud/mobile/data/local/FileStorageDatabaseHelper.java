package org.schulcloud.mobile.data.local;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.File;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import timber.log.Timber;

public class FileStorageDatabaseHelper extends BaseDatabaseHelper {

    @Inject
    FileStorageDatabaseHelper(Provider<Realm> realmProvider) {
        super(realmProvider);
    }

    @NonNull
    public Observable<List<File>> getFiles() {
        Realm realm = mRealmProvider.get();
        return realm.where(File.class).findAllAsync().asObservable()
                .filter(RealmResults::isLoaded)
                .map(realm::copyFromRealm);
    }
    @NonNull
    public Observable<File> setFiles(@NonNull Collection<File> files) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed())
                return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(files));
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
    public Observable<List<Directory>> getDirectories() {
        Realm realm = mRealmProvider.get();
        return realm.where(Directory.class).findAllAsync().asObservable()
                .filter(RealmResults::isLoaded)
                .map(realm::copyFromRealm);
    }
    @NonNull
    public Observable<Directory> setDirectories(@NonNull Collection<Directory> directories) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed())
                return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(directories));
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
}
