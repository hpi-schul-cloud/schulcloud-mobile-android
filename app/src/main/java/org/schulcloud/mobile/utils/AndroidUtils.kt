@file:Suppress("TooManyFunctions")
package org.schulcloud.mobile.utils

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.ArrayRes
import android.support.annotation.ColorInt
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.text.TextUtilsCompat
import android.support.v4.view.ViewCompat
import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.R
import java.util.*

fun logv(tag: String, msg: String, e: Exception? = null) {
    if (!BuildConfig.DEBUG)
        return
    if (e == null)
        Log.v(tag, msg)
    else
        Log.v(tag, msg, e)
}
fun logd(tag: String, msg: String, e: Exception? = null) {
    if (!BuildConfig.DEBUG)
        return
    if (e == null)
        Log.d(tag, msg)
    else
        Log.d(tag, msg, e)
}
fun logi(tag: String, msg: String, e: Exception? = null) {
    if (!BuildConfig.DEBUG)
        return
    if (e == null)
        Log.i(tag, msg)
    else
        Log.i(tag, msg, e)
}
fun logw(tag: String, msg: String, e: Exception? = null) {
    if (!BuildConfig.DEBUG)
        return
    if (e == null)
        Log.w(tag, msg)
    else
        Log.w(tag, msg, e)
}
fun loge(tag: String, msg: String, e: Exception? = null) {
    if (!BuildConfig.DEBUG)
        return
    if (e == null)
        Log.e(tag, msg)
    else
        Log.e(tag, msg, e)
}

fun Map<String, String>.asBundle(): Bundle {
    return Bundle().apply {
        for (entry in entries)
            putString(entry.key, entry.value)
    }
}

fun <T, R> LiveData<T>.map(func: (T) -> R): LiveData<R> = Transformations.map(this, func)
fun <T, R> LiveData<T>.switchMap(func: (T) -> LiveData<R>): LiveData<R> = Transformations.switchMap(this, func)

fun Drawable.asBitmap(): Bitmap {
    if (this is BitmapDrawable)
        return bitmap

    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}

fun Context.shareLink(url: String, titleContent: CharSequence? = null) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = MIME_TEXT_PLAIN
        putExtra(Intent.EXTRA_SUBJECT,
                if (titleContent != null)
                    getString(R.string.share_subject_format,
                            getString(R.string.brand_name),
                            titleContent)
                else getString(R.string.brand_name))
        putExtra(Intent.EXTRA_TEXT, url)
    }
    startActivity(Intent.createChooser(intent, getString(R.string.share_title)))
}

fun isLtr() = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_LTR

fun Context.getColorArray(@ArrayRes id: Int, @ColorInt fallback: Int? = null): IntArray {
    val fallbackColor = fallback ?: ResourcesCompat.getColor(resources, R.color.brand_accent, theme)
    val ta = resources.obtainTypedArray(id)
    val colors = IntArray(ta.length()) { i -> ta.getColor(i, fallbackColor) }
    ta.recycle()
    return colors
}
