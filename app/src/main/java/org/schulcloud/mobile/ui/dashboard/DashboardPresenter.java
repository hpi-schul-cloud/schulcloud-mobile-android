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

    private HomeworkDataManager mHomeworkDataManager;
    private EventDataManager mEventDataManager;
    private Subscription mSubscription;

    @Inject
    public DashboardPresenter(HomeworkDataManager homeworkDataManager,
                              EventDataManager eventDataManager) {
        mHomeworkDataManager = homeworkDataManager;
        mEventDataManager = eventDataManager;
    }

    @Override
    protected void onViewDetached() {
        super.onViewDetached();
        RxUtil.unsubscribe(mSubscription);
    }

    public void showHomework() {
        sendToView(view -> view.showOpenHomework(mHomeworkDataManager.getOpenHomeworks()));
    }

    public void showEvents() {
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mEventDataManager.getEventsForToday()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(events -> sendToView(view -> view.showEvents(events)));
    }

    public void showCourseDetails(@NonNull String courseId) {
        getViewOrThrow().showCourseDetails(courseId);
    }
}
