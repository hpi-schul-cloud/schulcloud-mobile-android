package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

/**
 * Date: 6/9/2018
 */
class CourseViewModelFactory(private val id: String) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CourseViewModel(id) as T
    }

}
