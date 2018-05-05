package org.schulcloud.mobile.ui.homework.detailed.feedback;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.datamanagers.SubmissionDataManager;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

/**
 * Date: 5/4/2018
 */
@ConfigPersistent
public class FeedbackPresenter extends BasePresenter<FeedbackMvpView> {
    private final SubmissionDataManager mDataManager;
    private Submission mSubmission;

    @Inject
    public FeedbackPresenter(SubmissionDataManager dataManager) {
        mDataManager = dataManager;
    }
    @Override
    public void onViewAttached(@NonNull FeedbackMvpView view) {
        super.onViewAttached(view);
        showSubmission();
    }

    void loadSubmission(@NonNull String homeworkId, @NonNull String studentId) {
        mSubmission = mDataManager.getSubmission(homeworkId, studentId);
        // No submission handled by view
        showSubmission();
    }
    private void showSubmission() {
        sendToView(v -> {
            if (mSubmission == null)
                v.showGrade(null, null);
            else
                v.showGrade(mSubmission.grade, mSubmission.gradeComment);
        });
    }
}
