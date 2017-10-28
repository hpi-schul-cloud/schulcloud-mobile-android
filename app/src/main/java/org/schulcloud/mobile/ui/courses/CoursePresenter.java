package org.schulcloud.mobile.ui.courses;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class CoursePresenter extends BasePresenter<CourseMvpView> {

    @Inject
    public CoursePresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(CourseMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null)
            mSubscription.unsubscribe();
    }

    public void loadCourses() {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getCourses()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Course>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading the users.");
                        getMvpView().showError();
                    }

                    @Override
                    public void onNext(List<Course> courses) {
                        if (courses.isEmpty()) {
                            getMvpView().showCoursesEmpty();
                        } else {
                            getMvpView().showCourses(courses);
                        }
                    }
                });
    }

    public void showCourseDetail(String courseId) {
        getMvpView().showCourseDialog(courseId);
    }

}
