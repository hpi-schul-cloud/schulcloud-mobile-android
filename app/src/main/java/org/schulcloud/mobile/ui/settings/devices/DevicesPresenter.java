package org.schulcloud.mobile.ui.settings.devices;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.requestBodies.DeviceRequest;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class DevicesPresenter extends BasePresenter<DevicesMvpView> {

    @Inject
    public DevicesPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSubscription);
    }

    public void registerDevice() {
        if (mDataManager.getPreferencesHelper().getMessagingToken().equals("null")) {
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.d("FirebaseID", "Refreshed token: " + token);

            Log.d("FirebaseID", "sending registration to Server");
            DeviceRequest deviceRequest = new DeviceRequest("firebase", "mobile",
                    android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")",
                    mDataManager.getCurrentUserId(), token, android.os.Build.VERSION.INCREMENTAL);

            RxUtil.unsubscribe(mSubscription);
            mSubscription = mDataManager.createDevice(deviceRequest, token)
                    .subscribe(
                            deviceResponse -> {},
                            throwable -> {},
                            () -> getMvpView().reloadDevices());
        }
    }
    public void loadDevices() {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getDevices()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        devices -> {
                            if (devices.isEmpty()) {
                                getMvpView().showDevicesEmpty();
                                // TODO: Show something
                            } else {
                                getMvpView().showDevices(devices);
                            }
                        },
                        //TODO: Show error
                        throwable -> Timber.e(throwable, "There was an error loading the users."));
    }
    public void deleteDevice(Device device) {
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.deleteDevice(device.token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        voidResponse -> {},
                        throwable -> {},
                        () -> {
                            getMvpView().reloadDevices();
                            mDataManager.getPreferencesHelper()
                                    .clear(PreferencesHelper.PREFERENCE_MESSAGING_TOKEN);
                        });
    }
}
