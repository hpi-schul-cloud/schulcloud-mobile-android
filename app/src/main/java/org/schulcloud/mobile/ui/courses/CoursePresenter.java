package org.schulcloud.mobile.ui.courses;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class CoursePresenter extends BasePresenter<CourseMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    CoursePresenter(DataManager dataManager) {
        mDataManager = dataManager;
        sendToView(v -> loadCourses());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtil.unsubscribe(mSubscription);
    }

    public void loadCourses() {
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getCourses()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        courses -> {
                            if (courses.isEmpty())
                                sendToView(CourseMvpView::showCoursesEmpty);
                            else
                                sendToView(view -> view.showCourses(courses));
                        },
                        error -> {
                            Timber.e(error, "There was an error loading the courses.");
                            sendToView(CourseMvpView::showError);
                        });
    }

    public void showCourseDetail(@NonNull String courseId) {
        getViewOrThrow().showCourseDetail(courseId);
    }
}
