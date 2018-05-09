package org.schulcloud.mobile.ui.homework.detailed;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import org.schulcloud.mobile.data.datamanagers.HomeworkDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

@ConfigPersistent
public class DetailedHomeworkPresenter extends BasePresenter<DetailedHomeworkMvpView> {

    private final HomeworkDataManager mHomeworkDataManager;
    private final UserDataManager mUserDataManager;
    private Homework mHomework;
    private User mStudent;

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

    public void init(@NonNull String homeworkId, @Nullable String studentId) {
        mHomework = mHomeworkDataManager.getHomeworkForId(homeworkId);
        if (mHomework == null)
            sendToView(DetailedHomeworkMvpView::showError_notFound);

        mStudent = mUserDataManager.getUser(studentId);
        showHomework();
    }
    @Nullable
    public ViewConfig getViewConfig() {
        if (mHomework == null)
            return null;

        return new ViewConfig(mHomework, mUserDataManager.getCurrentUserId(),
                mStudent != null ? mStudent._id : null);
    }
    private void showHomework() {
        sendToView(v -> {
            if (mHomework != null)
                v.showHomework(mHomework, mStudent);
        });
    }
}
