package org.schulcloud.mobile.ui.dashboard;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

@ConfigPersistent
public class DashboardPresenter extends BasePresenter<DashboardMvpView> {

    private DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    DashboardPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    protected void onViewDetached() {
        super.onViewDetached();
        RxUtil.unsubscribe(mSubscription);
    }

    public void showHomework() {
        sendToView(view -> view.showOpenHomework(mDataManager.getOpenHomeworks()));
    }

    public void showEvents() {
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getEventsForToday()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(events -> sendToView(view -> view.showEvents(events)));
    }

    public void showCourseDetails(@NonNull String courseId) {
        getViewOrThrow().showCourseDetails(courseId);
    }
}