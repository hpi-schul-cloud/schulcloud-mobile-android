package org.schulcloud.mobile.controllers.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.experimental.async
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.controllers.base.OnItemSelectedCallback
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.devices.DeviceRepository
import org.schulcloud.mobile.models.devices.DeviceRequest
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.viewmodels.DeviceListViewModel

class SettingsFragment: BaseFragment() {

    companion object {
        var TAG: String = SettingsFragment::class.java.simpleName

        class deviceCallback(): RequestJobCallback() {
            override fun onError(code: ErrorCode) {
                Log.i(TAG, ErrorCode.ERROR.toString())
            }

            override fun onSuccess() {
                async {DeviceRepository.syncDevices()}
            }
        }
    }

    class CreateDeviceCallback: RequestJobCallback(){
        override fun onError(code: ErrorCode) {
            Log.i(TAG,"Failed to register device:\n" + code)
        }

        override fun onSuccess() {
            Log.i(TAG,"Device registered!")
        }

    }

    fun openWebPage(url: String {
        val uris = Uri.parse(url)
        val webIntent = Intent(Intent.ACTION_VIEW, uris)
        val bundle = Bundle()
        bundle.putBoolean("new_window", true)
        webIntent.putExtras(bundle)
        this.context!!.startActivity(webIntent)
    }

    fun createDevice(){
        if(DeviceRepository.deviceToken == null) {
            DeviceRepository.deviceToken = FirebaseInstanceId.getInstance().token
        }

        DeviceRepository.createDevice(DeviceRequest("firebase","mobile",
                android.os.Build.MODEL + " ( " + android.os.Build.PRODUCT + " ) ",
                UserRepository.token!!,DeviceRepository.deviceToken!!,android.os.Build.VERSION.INCREMENTAL),CreateDeviceCallback())

    }

    fun openMail() {
        var emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "schul-cloud@hpi.de", null))

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Schul-Cloud Anfrage")
        this.context!!.startActivity(emailIntent)
    }

    private val deviceListAdapter by lazy {
        DeviceListAdapter(OnItemSelectedCallback {id ->
            DeviceRepository.deleteDevice(id,deviceCallback())
        })
    }

    private lateinit var viewModel: DeviceListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(DeviceListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.title = getString(R.string.settings)
        return inflater.inflate(R.layout.fragment_settings,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = swipeRefresh
        settings_open_source.setOnClickListener({v -> openWebPage(getString(R.string.settings_open_source_address))})
        settings_data_protection.setOnClickListener({v -> openWebPage(getString(R.string.settings_data_protection_address))})
        settings_imprint.setOnClickListener({v -> openWebPage(getString(R.string.settings_imprint))})
        settings_contact.setOnClickListener({v -> openMail()})
        settings_register_device.setOnClickListener({v -> createDevice()})
    }
}