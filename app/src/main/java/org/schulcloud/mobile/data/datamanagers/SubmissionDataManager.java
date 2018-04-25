package org.schulcloud.mobile.data.datamanagers;

import android.support.annotation.NonNull;
import android.util.Log;

import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.local.SubmissionDatabaseHelper;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.data.remote.RestService;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class SubmissionDataManager {
    private static final String TAG = SubmissionDataManager.class.getSimpleName();

    private final RestService mRestService;
    private final SubmissionDatabaseHelper mDatabaseHelper;

    @Inject
    UserDataManager userDataManager;

    @Inject
    public SubmissionDataManager(RestService restService, SubmissionDatabaseHelper databaseHelper) {
        mRestService = restService;
        mDatabaseHelper = databaseHelper;
    }

    @NonNull
    public Observable<Submission> syncSubmissions() {
        return mRestService.getSubmissions(userDataManager.getAccessToken())
                .concatMap(submissions -> {
                    // clear old submissions
                    mDatabaseHelper.clearTable(Submission.class);
                    return mDatabaseHelper.setSubmissions(submissions);
                })
                .doOnError(throwable -> Log.w(TAG, "Error while syncing submissions", throwable));
    }

    public Submission getSubmissionForId(String homeworkId) {
        return mDatabaseHelper.getSubmissionForId(homeworkId);
    }
}
