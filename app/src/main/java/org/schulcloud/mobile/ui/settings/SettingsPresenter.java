package org.schulcloud.mobile.ui.settings;

import android.util.Log;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.CalendarContentUtil;
import org.schulcloud.mobile.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class SettingsPresenter extends BasePresenter<SettingsMvpView> {

    @Inject
    public SettingsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(SettingsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void addEventsToLocalCalendar() {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getEvents()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Event>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading the events.");
                    }

                    @Override
                    public void onNext(List<Event> events) {
                        if (events.isEmpty()) {
                            getMvpView().showEventsEmpty();
                        } else {
                            getMvpView().connectToCalendar();
                        }
                    }
                });
    }


    public void checkSignedIn() {
        super.isAlreadySignedIn(mDataManager);
    }

}
