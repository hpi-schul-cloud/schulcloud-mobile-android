package org.schulcloud.mobile.data.local;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.model.Course;

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
public class CourseDatabaseHelper extends BaseDatabaseHelper {
    @Inject
    CourseDatabaseHelper(Provider<Realm> realmProvider) {super(realmProvider);}

    public Observable<Course> setCourses(final Collection<Course> newCourse) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed())
                return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(newCourse));
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
    public Observable<List<Course>> getCourses() {
        Realm realm = mRealmProvider.get();
        return realm.where(Course.class).findAllAsync().asObservable()
                .filter(RealmResults::isLoaded)
                .map(realm::copyFromRealm);
    }

    public Course getCourseForId(String courseId) {
        final Realm realm = mRealmProvider.get();
        return realm.where(Course.class).equalTo("_id", courseId).findFirst();
    }
}
