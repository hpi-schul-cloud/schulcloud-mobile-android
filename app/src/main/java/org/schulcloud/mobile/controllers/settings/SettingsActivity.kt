package org.schulcloud.mobile.controllers.settings

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import kotlinx.android.synthetic.main.activity_settings.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.models.notifications.Device
import org.schulcloud.mobile.models.notifications.NotificationRepository
import org.schulcloud.mobile.viewmodels.DeviceListViewModel
import org.schulcloud.mobile.viewmodels.SettingsViewModel

class SettingsActivity: BaseActivity() {

    companion object {
        var TAG: String = SettingsActivity::class.java.simpleName
    }

    fun openWebPage(url: String) {
        val uris = Uri.parse(url)
        val webIntent = Intent(Intent.ACTION_VIEW, uris)
        val bundle = Bundle()
        bundle.putBoolean("new_window", true)
        webIntent.putExtras(bundle)
        startActivity(webIntent)
    }

    fun openMail() {
        var emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "schul-cloud@hpi.de", null))

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Schul-Cloud Anfrage")
        startActivity(emailIntent)
    }

    private val deviceListAdapter by lazy {
        DeviceListAdapter({
            devicesViewModel.deleteDevice(it)
        }, devices_chevron, settings_devices_empty)
    }

    private lateinit var devicesViewModel: DeviceListViewModel
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        devicesViewModel = ViewModelProviders.of(this).get(DeviceListViewModel::class.java)
        settingsViewModel =  ViewModelProviders.of(this).get(SettingsViewModel::class.java)
    }

    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View {
        title = getString(R.string.settings)

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
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container,UserSettingsFragment(),UserSettingsFragment.TAG)
                    .addToBackStack(null)
                    .commit()
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
        return super.onCreateView(name, context, attrs)
    }

    override suspend fun refresh() {
        NotificationRepository.syncDevices()
    }
}