package org.schulcloud.mobile.ui.settings.devices;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.datamanagers.EventDataManager;
import org.schulcloud.mobile.data.datamanagers.NotificationDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.local.UserDatabaseHelper;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.requestBodies.DeviceRequest;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.ui.settings.SettingsMvpView;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class DevicesPresenter extends BasePresenter<DevicesMvpView> {

    @Inject
    public DevicesPresenter(UserDataManager userDataManager, NotificationDataManager notificationDataManager){
        mUserDataManager = userDataManager;
        mNotificationDataManager = notificationDataManager;
    }

    private Subscription mSubscription;
    private Subscription mEventsSubscription;
    private Subscription mDevicesSubscription;
    private NotificationDataManager mNotificationDataManager;
    private UserDataManager mUserDataManager;

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSubscription);
    }

    /* Notifications */
    public void registerDevice() {
        if (mNotificationDataManager.getPreferencesHelper().getMessagingToken().equals("null")) {
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.d("FirebaseID", "Refreshed token: " + token);

            Log.d("FirebaseID", "sending registration to Server");
            DeviceRequest deviceRequest = new DeviceRequest("firebase", "mobile",
                    android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")",
                    mUserDataManager.getCurrentUserId(), token, android.os.Build.VERSION.INCREMENTAL);

            RxUtil.unsubscribe(mEventsSubscription);
            mEventsSubscription = mNotificationDataManager.createDevice(deviceRequest, token)
                    .subscribe(
                            deviceResponse -> {},
                            throwable -> {},
                            () -> sendToView(SettingsMvpView::reloadDevices));
        }
    }
    public void loadDevices() {
        RxUtil.unsubscribe(mDevicesSubscription);
        mDevicesSubscription = mNotificationDataManager.getDevices()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        devices -> {
                            if (devices.isEmpty()) {
                                // TODO: Show something
                            } else
                                sendToView(view -> view.showDevices(devices));
                        },
                        //TODO: Show error
                        throwable -> Timber.e(throwable, "There was an error loading the users."));
    }
    public void deleteDevice(@NonNull Device device) {
        RxUtil.unsubscribe(mDevicesSubscription);
        mDevicesSubscription = mNotificationDataManager.deleteDevice(device.token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        voidResponse -> {},
                        throwable -> {},
                        () -> {
                            sendToView(SettingsMvpView::reloadDevices);
                            mNotificationDataManager.getPreferencesHelper()
                                    .clear(PreferencesHelper.PREFERENCE_MESSAGING_TOKEN);
                        });
    }
}
