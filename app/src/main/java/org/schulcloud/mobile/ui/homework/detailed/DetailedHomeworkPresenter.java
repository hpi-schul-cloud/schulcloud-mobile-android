package org.schulcloud.mobile.ui.homework.detailed;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.data.datamanagers.HomeworkDataManager;
import org.schulcloud.mobile.data.datamanagers.SubmissionDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

@ConfigPersistent
public class DetailedHomeworkPresenter extends BasePresenter<DetailedHomeworkMvpView> {

    private final HomeworkDataManager mHomeworkDataManager;
    private final SubmissionDataManager mSubmissionDataManager;
    private final UserDataManager mUserDataManager;
    private Homework mHomework;
    private Submission mSubmission;

    @Inject
    public DetailedHomeworkPresenter(HomeworkDataManager homeworkDataManager,
                                     SubmissionDataManager submissionDataManager,
                                     UserDataManager userDataManager) {
        mHomeworkDataManager = homeworkDataManager;
        mSubmissionDataManager = submissionDataManager;
        mUserDataManager = userDataManager;
    }

    @Override
    public void onViewAttached(@NonNull DetailedHomeworkMvpView view) {
        super.onViewAttached(view);
        showHomework();
    }

    /**
     * Loads a specific homework for a given id.
     *
     * @param homeworkId The id of the homework to be shown.
     */
    public void loadHomework(@NonNull String homeworkId) {
        mHomework = mHomeworkDataManager.getHomeworkForId(homeworkId);
        mSubmission = mSubmissionDataManager.getSubmissionForId(mHomework._id);
        sendToView(v -> v.showHomework(mHomeworkDataManager.getHomeworkForId(homeworkId)));
    }
    private void showHomework() {
        sendToView(v -> {
            if (mHomework != null)
                v.showHomework(mHomework);
            if (mSubmission != null)
                v.showSubmission(mSubmission, mUserDataManager.getCurrentUserId());
        });
    }
}
