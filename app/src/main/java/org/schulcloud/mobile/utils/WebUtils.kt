package org.schulcloud.mobile.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent.EXTRA_REFERRER
import android.net.Uri
import android.provider.Browser
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
const val HEADER_REFERER = "referer";

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
    }.build()
}

fun openUrl(context: Context, url: Uri, headers: Map<String, String>? = null) {
    Log.i(TAG, "Opening url: $url")
    newCustomTab(context).apply {
        headers?.also { intent.putExtra(Browser.EXTRA_HEADERS, headers.asBundle()) }

        intent.putExtra(EXTRA_REFERRER, Uri.parse("https://schul-cloud.org/courses/59a3c657a2049554a93fec3a/topics/5a7afee7994b406cfc028dd2/"))
    }.launchUrl(context, url)
}
