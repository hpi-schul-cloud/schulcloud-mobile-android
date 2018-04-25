package org.schulcloud.mobile.data.datamanagers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.schulcloud.mobile.data.local.HomeworkDatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.requestBodies.AddHomeworkRequest;
import org.schulcloud.mobile.data.model.responseBodies.AddHomeworkResponse;
import org.schulcloud.mobile.data.remote.RestService;
import org.schulcloud.mobile.util.Pair;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class HomeworkDataManager {
    private static final String TAG = HomeworkDataManager.class.getSimpleName();

    private final RestService mRestService;
    private final HomeworkDatabaseHelper mDatabaseHelper;

    @Inject
    PreferencesHelper mPreferencesHelper;
    @Inject
    UserDataManager userDataManager;

    @Inject
    public HomeworkDataManager(RestService restService, PreferencesHelper preferencesHelper,
            HomeworkDatabaseHelper databaseHelper) {
        mRestService = restService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
    }

    @NonNull
    public Observable<Homework> syncHomework() {
        return mRestService.getHomework(userDataManager.getAccessToken())
                .concatMap(homeworks -> {
                    // clear old devices
                    mDatabaseHelper.clearTable(Homework.class);
                    return mDatabaseHelper.setHomework(homeworks);
                })
                .doOnError(throwable -> Log.w(TAG, "Error while syncing homework", throwable));
    }

    @NonNull
    public Observable<List<Homework>> getHomework() {
        return mDatabaseHelper.getHomework().distinct();
    }
    @Nullable
    public Homework getHomeworkForId(String homeworkId) {
        return mDatabaseHelper.getHomeworkForId(homeworkId);
    }

    @NonNull
    public Pair<Integer, Date> getOpenHomeworks() {
        return mDatabaseHelper.getOpenHomeworks();
    }

    @NonNull
    public Observable<AddHomeworkResponse> addHomework(
            @NonNull AddHomeworkRequest addHomeworkRequest) {
        return mRestService.addHomework(userDataManager.getAccessToken(), addHomeworkRequest);
    }
}
