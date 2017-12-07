package org.schulcloud.mobile.ui.homework.detailed;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

@ConfigPersistent
public class DetailedHomeworkPresenter extends BasePresenter<DetailedHomeworkMvpView> {

    private DataManager mDataManager;

    @Inject
    public DetailedHomeworkPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    /**
     * Loads a specific homework for a given id.
     *
     * @param homeworkId The id of the homework to be shown.
     */
    public void loadHomework(@NonNull String homeworkId) {
        getViewOrThrow().showHomework(mDataManager.getHomeworkForId(homeworkId));
    }

    /**
     * Loads a specific submission containing the comments.
     *
     * @param homeworkId The id of the displayed homework. Required to reference the submission.
     */
    public void loadComments(@NonNull String homeworkId) {
        getViewOrThrow().showSubmission(mDataManager.getSubmissionForId(homeworkId),
                mDataManager.getCurrentUserId());
    }
}
