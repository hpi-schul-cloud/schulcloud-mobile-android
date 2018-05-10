package org.schulcloud.mobile.ui.homework.detailed;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import org.schulcloud.mobile.data.datamanagers.HomeworkDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.ListUtils;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;

import static org.schulcloud.mobile.util.ListUtils.contains;

@ConfigPersistent
public class DetailedHomeworkPresenter extends BasePresenter<DetailedHomeworkMvpView> {
    private final HomeworkDataManager mHomeworkDataManager;
    private final UserDataManager mUserDataManager;
    private Homework mHomework;
    private User mStudent;
    private boolean mFirstLoad = true;

    private Subscription sCurrentUser;

    @Inject
    public DetailedHomeworkPresenter(HomeworkDataManager homeworkDataManager,
            UserDataManager userDataManager) {
        mHomeworkDataManager = homeworkDataManager;
        mUserDataManager = userDataManager;
    }
    @Override
    public void onViewAttached(@NonNull DetailedHomeworkMvpView view) {
        super.onViewAttached(view);

        if (!mFirstLoad)
            showHomework();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtil.unsubscribe(sCurrentUser);
    }

    public void init(@NonNull String homeworkId, @Nullable String studentId) {
        mHomework = mHomeworkDataManager.getHomeworkForId(homeworkId);
        if (mHomework == null)
            sendToView(DetailedHomeworkMvpView::showError_notFound);

        // Show the current user is a student of this course, show his submission directly if none is specified
        if (studentId == null && contains(mHomework.courseId.userIds,
                id -> id.getValue().equalsIgnoreCase(mUserDataManager.getCurrentUserId()))) {
            RxUtil.unsubscribe(sCurrentUser);
            sCurrentUser = mUserDataManager.getCurrentUser()
                    .subscribe(user -> mStudent = User.from(user),
                            throwable -> {});
        } else
            mStudent = mUserDataManager.getUser(studentId);

        showHomework();
    }
    public void onStudentSelected(@NonNull User student) {
        mStudent = student;
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
            if (mHomework != null) {
                v.showHomework(mHomework, mStudent, !mFirstLoad);
                mFirstLoad = false;
            }
        });
    }
}
