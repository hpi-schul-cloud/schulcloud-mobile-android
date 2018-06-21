package org.schulcloud.mobile.views

import android.annotation.TargetApi
import android.content.Context
import android.net.Uri
import android.os.Build
import android.support.annotation.AttrRes
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import org.schulcloud.mobile.utils.openUrl


/**
 * Date: 6/15/2018
 */
class PassiveWebView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0)
    : ContentWebView(context, attrs, defStyleAttr) {
    init {
        isClickable = false
        isFocusable = false

        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                openUrl(Uri.parse(url))
                return true
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                openUrl(request.url)
                return true
            }

            private fun openUrl(url: Uri) {
                openUrl(context, url)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }
}
