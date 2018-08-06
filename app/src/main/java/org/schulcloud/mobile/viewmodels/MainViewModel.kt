package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.utils.SingleLiveEvent


class MainViewModel : ViewModel() {
    val config: MutableLiveData<MainFragmentConfig> = MutableLiveData()
    val onFabClicked: SingleLiveEvent<Void> = SingleLiveEvent()
}
