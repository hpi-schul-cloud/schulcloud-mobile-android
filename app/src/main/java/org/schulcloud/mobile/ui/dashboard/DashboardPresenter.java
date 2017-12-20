package org.schulcloud.mobile.ui.dashboard;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.datamanagers.EventDataManager;
import org.schulcloud.mobile.data.datamanagers.HomeworkDataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

@ConfigPersistent
public class DashboardPresenter extends BasePresenter<DashboardMvpView> {

    @Inject
    public DashboardPresenter(HomeworkDataManager homeworkDataManager,
                              EventDataManager eventDataManager) {
        mHomeworkDataManager = homeworkDataManager;
        mEventDataManager = eventDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSubscription);
    }

    public void showHomework() {
        getMvpView().showOpenHomework(mHomeworkDataManager.getOpenHomeworks());
    }

    public void showEvents() {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mEventDataManager.getEventsForToday()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(events -> getMvpView().showEvents(events));
    }

    public void showCourseDetails(String courseId) {
        getMvpView().showCourseDetails(courseId);
    }
}
