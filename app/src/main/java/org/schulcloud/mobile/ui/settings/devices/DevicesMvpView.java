package org.schulcloud.mobile.ui.settings.devices;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;


public interface DevicesMvpView extends MvpView {
    // Notifications
    void showDevices(@NonNull List<Device> devices);

    void reloadDevices();

    void showDevicesEmpty();

    void showDevicesError();
}
