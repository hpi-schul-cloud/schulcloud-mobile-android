package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FileViewModelFactory(private val owner: String, private val parent: String?) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == FileViewModel::class.java)
            FileViewModel(owner, parent) as T
        else
            throw IllegalArgumentException("Can't instantiate view model of type $modelClass")

    }
}
