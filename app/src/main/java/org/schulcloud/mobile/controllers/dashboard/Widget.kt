package org.schulcloud.mobile.controllers.dashboard

import androidx.fragment.app.Fragment

open class Widget : Fragment() {
    open suspend fun refresh() {}
}
