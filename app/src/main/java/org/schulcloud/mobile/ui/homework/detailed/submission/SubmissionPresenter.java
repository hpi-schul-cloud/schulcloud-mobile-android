package org.schulcloud.mobile.ui.homework.detailed.submission;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.datamanagers.SubmissionDataManager;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

/**
 * Date: 4/27/2018
 */
@ConfigPersistent
public class SubmissionPresenter extends BasePresenter<SubmissionMvpView> {
    private final SubmissionDataManager mDataManager;
    private Submission mSubmission;

    @Inject
    public SubmissionPresenter(SubmissionDataManager dataManager) {
        mDataManager = dataManager;
    }
    @Override
    public void onViewAttached(@NonNull SubmissionMvpView view) {
        super.onViewAttached(view);
        showSubmission();
    }

    void loadSubmission(@NonNull String homeworkId, @NonNull String studentId) {
        mSubmission = mDataManager.getSubmission(homeworkId, studentId);
        if (mSubmission == null)
            sendToView(SubmissionMvpView::showError_notFound);
        showSubmission();
    }
    private void showSubmission() {
        sendToView(v -> {
            if (mSubmission == null)
                return;
            v.showComment(mSubmission.comment);
        });
    }
}
