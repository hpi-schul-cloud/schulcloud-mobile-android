package org.schulcloud.mobile.ui.courses.detailed;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.datamanagers.CourseDataManager;
import org.schulcloud.mobile.data.datamanagers.TopicDataManager;
import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.model.Topic;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class DetailedCoursePresenter extends BasePresenter<DetailedCourseMvpView> {

    private Subscription mSubscription;
    private CourseDataManager mCourseDataManager;
    private TopicDataManager mTopicDataManager;

    @Inject
    public DetailedCoursePresenter(CourseDataManager dataManager, TopicDataManager topicDataManager) {
        mCourseDataManager = dataManager;
        mTopicDataManager = topicDataManager;
    }

    @Override
    protected void onViewDetached() {
        super.onViewDetached();
        RxUtil.unsubscribe(mSubscription);
    }

    /**
     * Loads a specific course for a given id.
     *
     * @param courseId The ID of the course to load.
     */
    public void loadCourse(@NonNull String courseId) {
        getViewOrThrow().showCourse(mCourseDataManager.getCourseForId(courseId));
    }

    public void loadTopics() {
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mTopicDataManager.getTopics()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        topics -> sendToView(view -> view.showTopics(topics)),
                        error -> {
                            Timber.e(error, "There was an error loading the topics.");
                            sendToView(DetailedCourseMvpView::showError);
                        });
    }

    public void showTopicDetail(@NonNull String topicId, @NonNull String topicName) {
        getViewOrThrow().showTopicDetail(topicId, topicName);
    }
}
