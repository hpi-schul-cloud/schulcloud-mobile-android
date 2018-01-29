package org.schulcloud.mobile.ui.homework.detailed;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

@ConfigPersistent
public class DetailedHomeworkPresenter extends BasePresenter<DetailedHomeworkMvpView> {

    private final DataManager mDataManager;
    private Homework mHomework;
    private Submission mSubmission;

    @Inject
    public DetailedHomeworkPresenter(DataManager dataManager) {
        mDataManager = dataManager;
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
        mHomework = mDataManager.getHomeworkForId(homeworkId);
        mSubmission = mDataManager.getSubmissionForId(mHomework._id);
        sendToView(v -> v.showHomework(mDataManager.getHomeworkForId(homeworkId)));
    }
    private void showHomework() {
        sendToView(v -> {
            if (mHomework != null)
                v.showHomework(mHomework);
            if (mSubmission != null)
                v.showSubmission(mSubmission, mDataManager.getCurrentUserId());
        });
    }
}
