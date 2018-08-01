@file:Suppress("TooManyFunctions")
package org.schulcloud.mobile.utils

import android.content.res.Resources
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import org.schulcloud.mobile.R


fun Int.dpToPx(): Int = Math.round(this * Resources.getSystem().displayMetrics.density)

fun Boolean.asVisibility(): Int {
    if (this)
        return View.VISIBLE
    return View.GONE
}

fun String?.toVisible(): Int = this.isNullOrEmpty().not().asVisibility()

@Suppress("SpreadOperator")
fun SwipeRefreshLayout.setup() {
    setColorSchemeColors(*context.getColorArray(R.array.brand_swipeRefreshColors))
}
