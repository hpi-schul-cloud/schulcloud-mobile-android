package org.schulcloud.mobile.data.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.schulcloud.mobile.data.model.Submission;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import timber.log.Timber;

@Singleton
public class SubmissionDatabaseHelper extends BaseDatabaseHelper {
    private static final String TAG = SubmissionDatabaseHelper.class.getSimpleName();

    @Inject
    SubmissionDatabaseHelper(Provider<Realm> realmProvider) {
        super(realmProvider);
    }

    public Observable<Submission> setSubmissions(@NonNull Collection<Submission> newSubmission) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed())
                return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(newSubmission));
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
    public Observable<List<Submission>> getSubmissions() {
        Realm realm = mRealmProvider.get();
        return realm.where(Submission.class).findAllAsync().asObservable()
                .filter(RealmResults::isLoaded)
                .map(realm::copyFromRealm);
    }

    @Nullable
    public Submission getSubmission(@NonNull String homeworkId, @NonNull String studentId) {
        return mRealmProvider.get().where(Submission.class)
                .equalTo("homeworkId", homeworkId)
                .equalTo("studentId", studentId)
                .findFirst();
    }
    @NonNull
    public Observable<List<Submission>> getSubmissionsForHomework(@NonNull String homeworkId) {
        List<Submission> s =
                mRealmProvider.get().where(Submission.class).equalTo("homeworkId", homeworkId)
                        .findAll();
        Realm realm = mRealmProvider.get();
        return realm.where(Submission.class).equalTo("homeworkId", homeworkId)
                .findAllAsync().asObservable()
                .filter(RealmResults::isLoaded)
                .map(realm::copyFromRealm)
                .doOnEach(notification -> {
                    Log.d(TAG, notification.toString());
                });
    }
}
