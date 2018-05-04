package org.schulcloud.mobile.ui.homework.detailed;

import android.support.annotation.NonNull;

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
    private final UserDataManager mUserDataManager;
    private Homework mHomework;

    @Inject
    public DetailedHomeworkPresenter(HomeworkDataManager homeworkDataManager,
            UserDataManager userDataManager) {
        mHomeworkDataManager = homeworkDataManager;
        mUserDataManager = userDataManager;
    }
    @Override
    public void onViewAttached(@NonNull DetailedHomeworkMvpView view) {
        super.onViewAttached(view);
        showHomework();
    }

    public void loadHomework(@NonNull String homeworkId) {
        mHomework = mHomeworkDataManager.getHomeworkForId(homeworkId);
        showHomework();
    }
    private void showHomework() {
        sendToView(v -> {
            if (mHomework != null)
                v.showHomework(mHomework, mUserDataManager.getCurrentUserId());
        });
    }
}
