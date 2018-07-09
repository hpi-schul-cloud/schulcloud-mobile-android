package org.schulcloud.mobile.controllers.main

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.generated.callback.OnClickListener
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_settings.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.viewmodels.DeviceListViewModel

class SettingsFragment: BaseFragment() {

    companion object{
        var TAG: String = SettingsFragment::class.java.simpleName

        fun openWebPage(url: String,mContext: Context){
            val uris = Uri.parse(url)
            val webIntent = Intent(Intent.ACTION_VIEW,uris)
            val bundle = Bundle()
            bundle.putBoolean("new_window",true)
            webIntent.putExtras(bundle)
            mContext.startActivity(webIntent)
        }

        fun openMail(mContext: Context){
            //TODO: finish
            var emailIntent = Intent(Intent.ACTION_SENDTO)
            var mailTo = Uri.parse("mailto:schul-cloud@hpi.de")

            emailIntent.setData(mailTo)

        }
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
        settings_open_source.setOnClickListener({v ->  openWebPage("https://github.com/schul-cloud/schulcloud-mobile-android",this.context!!)})
        settings_imprint.setOnClickListener({v -> openWebPage("https://schul-cloud.org/impressum",this.context!!)})
        settings_data_protection.setOnClickListener({v -> openWebPage("https://schul-cloud.org/impressum#data_security",this.context!!)})

    }
}