package org.schulcloud.mobile.controllers.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.experimental.async
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.controllers.base.OnItemSelectedCallback
import org.schulcloud.mobile.jobs.CreateDeviceJob
import org.schulcloud.mobile.jobs.DeleteDeviceJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.devices.DeviceRepository
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
    }
}