package org.schulcloud.mobile.utils

import android.content.res.Resources
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.schulcloud.mobile.R


/**
 * Date: 6/11/2018
 */

fun Int.dpToPx(): Int = Math.round(this * Resources.getSystem().displayMetrics.density)

fun Boolean.asVisibility(): Int {
    if (this)
        return View.VISIBLE
    return View.GONE
}

fun String?.toVisible(): Int = this.isNullOrEmpty().not().asVisibility()

fun SwipeRefreshLayout.setup() {
    setColorSchemeColors(
            ResourcesCompat.getColor(context.resources, R.color.hpiRed, context.theme),
            ResourcesCompat.getColor(context.resources, R.color.hpiOrange, context.theme),
            ResourcesCompat.getColor(context.resources, R.color.hpiYellow, context.theme))
}

fun SwipeRefreshLayout.syncOnRefresh(sync: suspend () -> Unit) {
    setup()
    setOnRefreshListener {
        async(UI) {
            sync()
        }
        isRefreshing = false
    }
}
