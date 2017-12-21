package org.schulcloud.mobile.ui.homework.detailed;

import android.support.annotation.NonNull;

<<<<<<< 488a63dc89c06296c9017298deab1621e8f58c25
import org.schulcloud.mobile.data.DataManager;
<<<<<<< 9cba5cf22b8ef44549486cd749b09223db48fc3e
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.Submission;
=======
>>>>>>> split DataManagers/DatabaseHelpers and updated for the new builds, need to fix Tests
=======
>>>>>>> removed usage of DatabaseHelper and DatManager completely in code, cleaned up imports( removed DataManager and DatabaseHelper from Imports)
import org.schulcloud.mobile.data.datamanagers.HomeworkDataManager;
import org.schulcloud.mobile.data.datamanagers.SubmissionDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

@ConfigPersistent
public class DetailedHomeworkPresenter extends BasePresenter<DetailedHomeworkMvpView> {

<<<<<<< 9cba5cf22b8ef44549486cd749b09223db48fc3e
    private final HomeworkDataManager mHomeworkDataManager;
    private final SubmissionDataManager mSubmissionDataManager;
    private final UserDataManager mUserDataManager;
    private Homework mHomework;
    private Submission mSubmission;
=======
    private HomeworkDataManager mHomeworkDataManager;
    private SubmissionDataManager mSubmissionDataManager;
    private UserDataManager mUserDataManager;
>>>>>>> split DataManagers/DatabaseHelpers and updated for the new builds, need to fix Tests

    @Inject
    public DetailedHomeworkPresenter(HomeworkDataManager homeworkDataManager,
                                     SubmissionDataManager submissionDataManager,
                                     UserDataManager userDataManager) {
        mHomeworkDataManager = homeworkDataManager;
        mSubmissionDataManager = submissionDataManager;
        mUserDataManager = userDataManager;
<<<<<<< 9cba5cf22b8ef44549486cd749b09223db48fc3e
    }

    @Override
    public void onViewAttached(@NonNull DetailedHomeworkMvpView view) {
        super.onViewAttached(view);
        showHomework();
=======
>>>>>>> split DataManagers/DatabaseHelpers and updated for the new builds, need to fix Tests
    }

    /**
     * Loads a specific homework for a given id.
     *
     * @param homeworkId The id of the homework to be shown.
     */
    public void loadHomework(@NonNull String homeworkId) {
<<<<<<< 9cba5cf22b8ef44549486cd749b09223db48fc3e
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
=======
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
>>>>>>> split DataManagers/DatabaseHelpers and updated for the new builds, need to fix Tests
    }
}
