package org.schulcloud.mobile.data.local;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.model.Topic;

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
public class TopicsDatabaseHelper extends BaseDatabaseHelper {
    @Inject
    TopicsDatabaseHelper(Provider<Realm> realmProvider) {
        super(realmProvider);
    }

    @NonNull
    public Observable<Topic> setTopics(@NonNull String courseId,
            @NonNull Collection<Topic> newTopics) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed())
                return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> {
                    realm1.where(Topic.class).equalTo("courseId", courseId).findAll()
                            .deleteAllFromRealm();
                    realm1.copyToRealmOrUpdate(newTopics);
                });
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null)
                    realm.close();
            }
        });
    }
    @NonNull
    public Observable<Topic> setTopic(@NonNull Topic topic) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed())
                return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealm(topic));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null)
                    realm.close();
            }
        });
    }

    @NonNull
    public Observable<List<Topic>> getTopics(@NonNull String courseId) {
        Realm realm = mRealmProvider.get();
        return realm.where(Topic.class).equalTo("courseId", courseId).findAllAsync().asObservable()
                .filter(RealmResults::isLoaded)
                .map(realm::copyFromRealm);
    }

    public Topic getTopicForId(@NonNull String topicId) {
        return mRealmProvider.get().where(Topic.class).equalTo("_id", topicId).findFirst();
    }
}
