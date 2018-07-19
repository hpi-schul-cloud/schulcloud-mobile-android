package org.schulcloud.mobile.views

import android.content.Context
import android.support.annotation.AttrRes
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebViewClient


/**
 * Date: 6/15/2018
 */
class PassiveWebView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0)
    : ContentWebView(context, attrs, defStyleAttr) {
    init {
        isClickable = false
        isFocusable = false

        webViewClient = WebViewClient()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }
}
