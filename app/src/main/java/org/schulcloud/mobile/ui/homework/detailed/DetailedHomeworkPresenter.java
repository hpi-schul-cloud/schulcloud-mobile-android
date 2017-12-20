package org.schulcloud.mobile.ui.homework.detailed;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.datamanagers.HomeworkDataManager;
import org.schulcloud.mobile.data.datamanagers.SubmissionDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

@ConfigPersistent
public class DetailedHomeworkPresenter extends BasePresenter<DetailedHomeworkMvpView> {

    private HomeworkDataManager mHomeworkDataManager;
    private SubmissionDataManager mSubmissionDataManager;
    private UserDataManager mUserDataManager;

    @Inject
    public DetailedHomeworkPresenter(HomeworkDataManager homeworkDataManager,
                                     SubmissionDataManager submissionDataManager,
                                     UserDataManager userDataManager) {
        mHomeworkDataManager = homeworkDataManager;
        mSubmissionDataManager = submissionDataManager;
        mUserDataManager = userDataManager;
    }

    /**
     * Loads a specific homework for a given id.
     *
     * @param homeworkId The id of the homework to be shown.
     */
    public void loadHomework(@NonNull String homeworkId) {
        getViewOrThrow().showHomework(mHomeworkDataManager.getHomeworkForId(homeworkId));
    }

    /**
     * Loads a specific submission containing the comments.
     *
     * @param homeworkId The id of the displayed homework. Required to reference the submission.
     */
    public void loadComments(@NonNull String homeworkId) {
        getViewOrThrow().showSubmission(mSubmissionDataManager.getSubmissionForId(homeworkId),
                mUserDataManager.getCurrentUserId());
    }
}
