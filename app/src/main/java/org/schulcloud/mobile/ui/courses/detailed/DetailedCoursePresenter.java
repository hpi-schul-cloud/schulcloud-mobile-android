package org.schulcloud.mobile.ui.courses.detailed;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class DetailedCoursePresenter extends BasePresenter<DetailedCourseMvpView> {

    @Inject
    public DetailedCoursePresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(DetailedCourseMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    /**
     * loads a specific course for a given id.
     * @param courseId given id for referencing.
     */
    public void loadCourse(String courseId) {
        checkViewAttached();
        getMvpView().showCourse(mDataManager.getCourseForId(courseId));
    }

    public void loadTopics() {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getTopics()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        topics -> {
                            getMvpView().showTopics(topics);
                        },
                        // onError
                        error -> {
                            Timber.e(error, "There was an error loading the topics.");
                            getMvpView().showError();
                        },
                        () -> {
                        });
    };
}
