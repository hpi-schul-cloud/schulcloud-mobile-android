package org.schulcloud.mobile.ui.homework.detailed.submissions;

import android.os.UserManager;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.Log;

import org.schulcloud.mobile.data.datamanagers.CourseDataManager;
import org.schulcloud.mobile.data.datamanagers.HomeworkDataManager;
import org.schulcloud.mobile.data.datamanagers.SubmissionDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.ListUtils;
import org.schulcloud.mobile.util.RxUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Date: 5/5/2018
 */
@ConfigPersistent
public class SubmissionsPresenter extends BasePresenter<SubmissionsMvpView> {
    private static final String TAG = SubmissionsPresenter.class.getSimpleName();

    private final UserDataManager mUserDataManager;
    private final HomeworkDataManager mHomeworkDataManager;
    private final CourseDataManager mCourseDataManager;
    private final SubmissionDataManager mDataManager;
    private Subscription sSubmissions;

    @Inject
    public SubmissionsPresenter(UserDataManager userDataManager,
            HomeworkDataManager homeworkDataManager, CourseDataManager courseDataManager,
            SubmissionDataManager dataManager) {
        mUserDataManager = userDataManager;
        mHomeworkDataManager = homeworkDataManager;
        mCourseDataManager = courseDataManager;
        mDataManager = dataManager;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtil.unsubscribe(sSubmissions);
    }
    void loadSubmissions(@NonNull String homeworkId) {
        Homework homework = mHomeworkDataManager.getHomeworkForId(homeworkId);
        if (homework == null) {
            sendToView(SubmissionsMvpView::showError);
            return;
        }
        Course course = mCourseDataManager.getCourseForId(homework.courseId._id);
        if (course == null) {
            sendToView(SubmissionsMvpView::showError);
            return;
        }
        String currentUserId = mUserDataManager.getCurrentUserId();

        RxUtil.unsubscribe(sSubmissions);
        sSubmissions = mDataManager.getSubmissionsForHomework(homeworkId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(submissions -> {
                    if (submissions.isEmpty())
                        sendToView(v -> v.showSubmissionsEmpty(currentUserId, homework));
                    else {
                        List<User> users = course.userIds;
                        List<Pair<User, Submission>> subs = new ArrayList<>();
                        for (User user : users)
                            subs.add(new Pair<>(user, ListUtils.where(submissions,
                                    submission -> user._id
                                            .equalsIgnoreCase(submission.studentId))));
                        sendToView(v -> v.showSubmissions(currentUserId, homework, subs));
                    }
                }, throwable -> {
                    Log.w(TAG, "Error getting submissions", throwable);
                    sendToView(SubmissionsMvpView::showError);
                });
    }
}
