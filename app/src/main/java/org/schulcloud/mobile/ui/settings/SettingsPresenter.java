package org.schulcloud.mobile.ui.settings;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.model.requestBodies.DeviceRequest;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
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
                            // todo: start connecting
                        }
                    }
                });
    }


    public void checkSignedIn() {
        super.isAlreadySignedIn(mDataManager);
    }

    public void registerDevice() {

        if (mDataManager.getPreferencesHelper().getMessagingToken().equals("null")) {
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.d("FirebaseID", "Refreshed token: " + token);

            Log.d("FirebaseID", "sending registration to Server");
            DeviceRequest deviceRequest = new DeviceRequest("firebase", "mobile", android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")", mDataManager.getCurrentUserId(), token, android.os.Build.VERSION.INCREMENTAL);

            if (mSubscription != null && !mSubscription.isUnsubscribed())
                mSubscription.unsubscribe();
            mSubscription = mDataManager.createDevice(deviceRequest, token)
                    .subscribe();
        }
    }

    public void unregisterDevice() {
        // TODO: To be implemented
    }

    public void loadDevices() {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getDevices()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Device>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading the users.");
                        //TODO: Show error
                    }

                    @Override
                    public void onNext(List<Device> devices) {
                        if (devices.isEmpty()) {
                            // TODO: Show something
                        } else {
                            getMvpView().showDevices(devices);
                        }
                    }
                });
    }

}
