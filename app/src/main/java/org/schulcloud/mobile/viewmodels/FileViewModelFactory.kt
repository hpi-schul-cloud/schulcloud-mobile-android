package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FileViewModelFactory(private val refOwnerModel: String, private val owner: String, private val parent: String?) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == FileViewModel::class.java)
            FileViewModel(refOwnerModel, owner, parent) as T
        else
            throw IllegalArgumentException("Can't instantiate view model of type $modelClass")

    }
}
