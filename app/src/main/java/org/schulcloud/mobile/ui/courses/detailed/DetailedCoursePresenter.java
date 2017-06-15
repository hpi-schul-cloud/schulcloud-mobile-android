package org.schulcloud.mobile.ui.courses.detailed;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

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
}
