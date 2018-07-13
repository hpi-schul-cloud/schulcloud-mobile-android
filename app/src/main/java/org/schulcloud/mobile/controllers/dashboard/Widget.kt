package org.schulcloud.mobile.controllers.dashboard

import android.support.v4.app.Fragment

open class Widget : Fragment() {
    open suspend fun refresh() {}
}
