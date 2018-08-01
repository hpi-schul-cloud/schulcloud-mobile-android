package org.schulcloud.mobile.utils

import android.content.res.Resources
import androidx.databinding.BindingAdapter
import androidx.annotation.ColorInt
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.view.View
import android.widget.ImageView
import org.schulcloud.mobile.R


/**
 * Date: 6/11/2018
 */

// to be used instead of android:tint for backwards compatibility
@BindingAdapter("colorFilter")
fun setColor(view: ImageView, @ColorInt color: Int){
    view.setColorFilter(color)
}

fun Int.dpToPx(): Int = Math.round(this * Resources.getSystem().displayMetrics.density)

fun Boolean.asVisibility(): Int {
    if (this)
        return View.VISIBLE
    return View.GONE
}

fun String?.toVisible(): Int = this.isNullOrEmpty().not().asVisibility()

fun SwipeRefreshLayout.setup() {
    setColorSchemeColors(*context.getColorArray(R.array.brand_swipeRefreshColors))
}

var View.visibilityBool: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }
