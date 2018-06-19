package org.schulcloud.mobile.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.util.Log
import org.schulcloud.mobile.R
import org.schulcloud.mobile.config.Config


/**
 * Date: 6/11/2018
 */
const val HEADER_COOKIE = "cookie"
const val HEADER_CONTENT_TYPE = "content-type"
const val HEADER_CONTENT_ENCODING = "content-encoding";

const val MIME_TEXT_PLAIN = "text/plain"
const val MIME_TEXT_HTML = "text/html"
const val MIME_APPLICATION_JSON = "application/json"

const val ENCODING_UTF_8 = "utf-8"

val HOST = Config.API_URL.substringBeforeLast(":")


fun String?.asUri(): Uri {
    return if (this == null)
        Uri.EMPTY
    else
        Uri.parse(this)
}

fun newCustomTab(context: Context): CustomTabsIntent {
    return CustomTabsIntent.Builder().apply {
        setToolbarColor(ContextCompat.getColor(context, R.color.hpiRed))
        addDefaultShareMenuItem()
        setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left)
        setExitAnimations(context, R.anim.slide_in_left, R.anim.slide_out_right)
    }.build()
}

fun openUrl(context: Context, url: Uri) {
    Log.i(TAG, "Opening url: $url")
    newCustomTab(context).launchUrl(context, url)
}
