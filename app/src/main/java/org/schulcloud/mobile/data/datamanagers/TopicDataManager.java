package org.schulcloud.mobile.data.datamanagers;

import android.support.annotation.NonNull;
import android.util.Log;

import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.local.TopicsDatabaseHelper;
import org.schulcloud.mobile.data.model.Topic;
import org.schulcloud.mobile.data.remote.RestService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Single;

@Singleton
public class TopicDataManager {
    private static final String TAG = TopicDataManager.class.getSimpleName();

    private final RestService mRestService;
    private final TopicsDatabaseHelper mDatabaseHelper;
    private final PreferencesHelper mPreferencesHelper;
    private final UserDataManager mUserDataManager;

    @Inject
    public TopicDataManager(RestService restService, PreferencesHelper preferencesHelper,
            TopicsDatabaseHelper databaseHelper, UserDataManager userDataManager) {
        mRestService = restService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
        mUserDataManager = userDataManager;
    }

    @NonNull
    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    @NonNull
    public Observable<Topic> syncTopics(@NonNull String courseId) {
        return mRestService.getTopics(mUserDataManager.getAccessToken(), courseId)
                .concatMap(topics -> mDatabaseHelper.setTopics(courseId, topics.data))
                .doOnError(throwable ->
                        Log.w(TAG, "Error syncing topics for course " + courseId, throwable));
    }

    @NonNull
    public Observable<List<Topic>> getTopics(@NonNull String courseId) {
        return mDatabaseHelper.getTopics(courseId);
    }
    @NonNull
    public Single<Topic> getTopic(@NonNull String topicId) {
        Topic topic = mDatabaseHelper.getTopicForId(topicId);
        if (topic != null)
            return Single.just(topic);

        return mRestService.getTopic(mUserDataManager.getAccessToken(), topicId)
                .doOnSuccess(mDatabaseHelper::setTopic);
    }
}
