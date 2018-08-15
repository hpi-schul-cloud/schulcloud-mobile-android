package org.schulcloud.mobile.controllers.base

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    val baseActivity: BaseActivity? get() = activity as? BaseActivity

    suspend fun requestPermission(permission: String): Boolean = baseActivity?.requestPermission(permission) ?: false
}
