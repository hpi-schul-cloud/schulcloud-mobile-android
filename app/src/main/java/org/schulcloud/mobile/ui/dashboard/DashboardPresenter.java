package org.schulcloud.mobile.ui.dashboard;


import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.datamanagers.EventDataManager;
import org.schulcloud.mobile.data.datamanagers.HomeworkDataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

@ConfigPersistent
public class DashboardPresenter extends BasePresenter<DashboardMvpView> {

    private final HomeworkDataManager mHomeworkDataManager;
    private final EventDataManager mEventDataManager;
    private Subscription mSubscription;

    @Inject

    public DashboardPresenter(HomeworkDataManager homeworkDataManager,
                              EventDataManager eventDataManager) {
        reload();
        mHomeworkDataManager = homeworkDataManager;
        mEventDataManager = eventDataManager;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtil.unsubscribe(mSubscription);
    }

    public void reload() {
        sendToView(v -> v.showOpenHomework(mHomeworkDataManager.getOpenHomeworks()));

        RxUtil.unsubscribe(mSubscription);
        mSubscription = mEventDataManager.getEventsForToday()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(events -> sendToView(view -> view.showEvents(events)));
    }

    public void showCourseDetails(@NonNull String courseId) {
        getViewOrThrow().showCourseDetails(courseId);
    }
}
