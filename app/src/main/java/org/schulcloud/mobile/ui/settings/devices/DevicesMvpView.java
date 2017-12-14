package org.schulcloud.mobile.ui.settings.devices;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

/**
 * Created by araknor on 14.12.17.
 */

public interface DevicesMvpView extends MvpView {
    // Notifications
    void showDevices(@NonNull List<Device> devices);

    void reloadDevices();
}
