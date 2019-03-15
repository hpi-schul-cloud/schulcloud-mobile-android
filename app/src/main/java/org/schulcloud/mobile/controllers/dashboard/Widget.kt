package org.schulcloud.mobile.controllers.dashboard

import androidx.fragment.app.Fragment
import org.schulcloud.mobile.controllers.main.Refreshable
import org.schulcloud.mobile.controllers.main.RefreshableImpl

abstract class Widget(refreshableImpl: RefreshableImpl = RefreshableImpl()) : Fragment(),
        Refreshable by refreshableImpl {
    init {
        refreshableImpl.refresh = { refresh() }
    }

    abstract suspend fun refresh()
}
