package org.schulcloud.mobile.ui.homework.detailed;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

@ConfigPersistent
public class DetailedHomeworkPresenter extends BasePresenter<DetailedHomeworkMvpView> {

    private DataManager mDataManager;
    private Homework mHomework;

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
        mHomework = mDataManager.getHomeworkForId(homeworkId);
        sendToView(v -> v.showHomework(mDataManager.getHomeworkForId(homeworkId)));
        loadSubmission();
    }

    /**
     * Loads a specific submission containing the comments.
     */
    public void loadSubmission() {
        sendToView(v -> v.showSubmission(mDataManager.getSubmissionForId(mHomework._id),
                mDataManager.getCurrentUserId()));
    }
}
