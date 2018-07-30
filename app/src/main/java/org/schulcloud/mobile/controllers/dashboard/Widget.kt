package org.schulcloud.mobile.controllers.dashboard

import android.support.v4.app.Fragment
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.controllers.main.MainActivity

open class Widget : Fragment() {
    fun showFragment(fragment: BaseFragment, tag: String) {
        // TODO: doesn't update NavDrawer
        (activity as MainActivity).replaceFragment(fragment, tag)
    }

    open suspend fun refresh() {}
}
