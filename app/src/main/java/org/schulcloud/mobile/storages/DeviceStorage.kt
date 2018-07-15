package org.schulcloud.mobile.storages

import android.content.Context
import org.schulcloud.mobile.storages.base.BaseStorage

class DeviceStorage: BaseStorage(PREF_DEVICE, Context.MODE_PRIVATE){
    companion object {
        var PREF_DEVICE: String = "pref_device"
        var DEVICE_TOKEN: String = "device_token"
    }

    var deviceToken: String?
        get() = getString(DEVICE_TOKEN)
        set(value: String?) = putString(DEVICE_TOKEN,value)
}