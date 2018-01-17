package org.schulcloud.mobile.ui.courses.detailed;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class DetailedCoursePresenter extends BasePresenter<DetailedCourseMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;
    private Course mCourse;

    @Inject
    DetailedCoursePresenter(DataManager dataManager) {
        mDataManager = dataManager;
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
        mCourse = mDataManager.getCourseForId(courseId);
        sendToView(v -> v.showCourseName(mCourse.name));
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getTopics()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        topics -> sendToView(v -> v.showTopics(topics)),
                        error -> {
                            Timber.e(error, "There was an error loading the topics.");
                            sendToView(DetailedCourseMvpView::showError);
                        });
    }

    public void showTopicDetail(@NonNull String topicId) {
        getViewOrThrow().showTopicDetail(topicId);
    }
}
