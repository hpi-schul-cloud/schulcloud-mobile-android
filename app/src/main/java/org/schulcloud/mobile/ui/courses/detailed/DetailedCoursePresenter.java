package org.schulcloud.mobile.ui.courses.detailed;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.datamanagers.TopicDataManager;
import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.datamanagers.CourseDataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class DetailedCoursePresenter extends BasePresenter<DetailedCourseMvpView> {

    private CourseDataManager mCourseDataManager;
    private TopicDataManager mTopicDataManager;
    private Subscription mSubscription;
    private Course mCourse;

    @Inject
    public DetailedCoursePresenter(CourseDataManager courseDataManager, TopicDataManager topicDataManager) {
        mCourseDataManager = courseDataManager;
        mTopicDataManager = topicDataManager;
    }

    @Override
    public void onViewAttached(@NonNull DetailedCourseMvpView view) {
        super.onViewAttached(view);

        showName();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtil.unsubscribe(mSubscription);
    }

    /**
     * Loads a specific course for a given id.
     *
     * @param courseId The ID of the course to load.
     */
    public void loadCourse(@NonNull String courseId) {
        mCourse = mCourseDataManager.getCourseForId(courseId);
        showName();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mTopicDataManager.getTopics()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        topics -> sendToView(v -> v.showTopics(topics)),
                        error -> {
                            Timber.e(error, "There was an error loading the topics.");
                            sendToView(DetailedCourseMvpView::showError);
                        });
    }
    private void showName() {
        sendToView(v -> {
            if (mCourse == null)
                return;
            v.showCourseName(mCourse.name);
        });
    }

    public void showTopicDetail(@NonNull String topicId) {
        sendToView(v -> v.showTopicDetail(topicId));
    }
}
