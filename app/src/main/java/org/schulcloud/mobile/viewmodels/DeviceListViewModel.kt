package org.schulcloud.mobile.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import io.realm.Realm
import io.realm.RealmResults
import org.schulcloud.mobile.models.devices.Device
import org.schulcloud.mobile.models.devices.DeviceRepository

class DeviceListViewModel(application: Application): AndroidViewModel(application){

    fun openWebPage(url: String, mContext: Context) {
        val uris = Uri.parse(url)
        val webIntent = Intent(Intent.ACTION_VIEW, uris)
        val bundle = Bundle()
        bundle.putBoolean("new_window", true)
        webIntent.putExtras(bundle)
        mContext.startActivity(webIntent)
    }

    fun openMail(mContext: Context) {
        var emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "schul-cloud@hpi.de", null))

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Schul-Cloud Anfrage")
        mContext.startActivity(emailIntent)
    }

    private val realm by lazy{
        Realm.getDefaultInstance()
    }

    private var devices: LiveData<RealmResults<Device>?> = DeviceRepository.getDevices(realm)

    fun getDevices(): LiveData<RealmResults<Device>?>{
        return devices
    }
}