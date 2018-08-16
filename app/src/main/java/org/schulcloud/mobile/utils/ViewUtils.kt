@file:Suppress("TooManyFunctions")

package org.schulcloud.mobile.utils

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.format.DateUtils
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.schulcloud.mobile.R

@BindingConversion
@ColorInt
fun convertStringToColor(color: String) = Color.parseColor(color)

@BindingConversion
fun convertStringToDrawable(color: String) = ColorDrawable(Color.parseColor(color))

@BindingAdapter("displayDate")
fun showDate(view: TextView, date: String?) {
    view.text = date?.parseDate()?.let {
        DateUtils.formatDateTime(view.context, it.timeInMillis, DateUtils.FORMAT_SHOW_DATE)
    } ?: ""
}

fun Int.dpToPx(): Int = Math.round(this * Resources.getSystem().displayMetrics.density)

@BindingConversion
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

var View.visibilityBool: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }
