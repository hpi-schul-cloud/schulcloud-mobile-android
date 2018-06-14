package org.schulcloud.mobile.utils

import android.content.res.Resources
import android.view.View

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
