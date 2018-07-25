package org.schulcloud.mobile.controllers.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_settings.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.controllers.base.OnItemSelectedCallback
import org.schulcloud.mobile.controllers.course.CourseActivity
import org.schulcloud.mobile.controllers.user_settings.UserSettingsActivity
import org.schulcloud.mobile.models.notifications.Device
import org.schulcloud.mobile.models.notifications.NotificationRepository
import org.schulcloud.mobile.viewmodels.DeviceListViewModel
import org.schulcloud.mobile.viewmodels.SettingsViewModel
import org.schulcloud.mobile.viewmodels.UserSettingsViewModel

class SettingsFragment: BaseFragment() {

    companion object {
        var TAG: String = SettingsFragment::class.java.simpleName
    }

    fun openWebPage(url: String) {
        val uris = Uri.parse(url)
        val webIntent = Intent(Intent.ACTION_VIEW, uris)
        val bundle = Bundle()
        bundle.putBoolean("new_window", true)
        webIntent.putExtras(bundle)
        this.context!!.startActivity(webIntent)
    }

    fun openMail() {
        var emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "schul-cloud@hpi.de", null))

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Schul-Cloud Anfrage")
        this.context!!.startActivity(emailIntent)
    }

    private val deviceListAdapter by lazy {
        DeviceListAdapter(OnItemSelectedCallback {id ->
            devicesViewModel.deleteDevice(id)
        }, devices_chevron, settings_devices_empty)
    }

    private lateinit var devicesViewModel: DeviceListViewModel
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        devicesViewModel = ViewModelProviders.of(this).get(DeviceListViewModel::class.java)
        settingsViewModel =  ViewModelProviders.of(this).get(SettingsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.title = getString(R.string.settings)
        return inflater.inflate(R.layout.fragment_settings,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = settings_swipeRefresh

        settings_open_source.setOnClickListener({openWebPage(getString(R.string.settings_open_source_address))})
        settings_data_protection.setOnClickListener({openWebPage(getString(R.string.settings_data_protection_address))})
        settings_imprint.setOnClickListener({openWebPage(getString(R.string.settings_imprint))})
        settings_contact.setOnClickListener({openMail()})
        settings_register_device.setOnClickListener({devicesViewModel.createDevice()})

        settingsViewModel.user.observe(this, Observer { user ->
            user_settings_forename.text = user!!.firstName
            user_settings_lastname.text = user.lastName
            user_settings_email.text = user.email
            user_settings_gender.setSelection(resources.getStringArray(R.array.genders)
                    .indexOf(user.gender))
        })

        user_settings_open.setOnClickListener {
            val intent = Intent(activity,UserSettingsActivity::class.java)
            activity?.startActivity(intent)
        }

        settings_devices_list.setOnClickListener({devicesViewModel.getDevices().observe(this, Observer {
            devices -> deviceListAdapter.changeState(devices as List<Device>)
        })})

        settings_devices.apply {
            adapter = deviceListAdapter
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        }

        for(string in resources.getStringArray(R.array.contributors_names)) run {
            settings_contributor_list.append(string + "\n")
        }
    }

    override suspend fun refresh() {
        NotificationRepository.syncDevices()
    }
}