@file:Suppress("TooManyFunctions")

package org.schulcloud.mobile.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.annotation.ArrayRes
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import org.schulcloud.mobile.R
import java.util.*


fun Map<String, String>.asBundle(): Bundle {
    return Bundle().apply {
        for (entry in entries)
            putString(entry.key, entry.value)
    }
}

fun <T> T?.asLiveData(): LiveData<T> = MutableLiveData<T>().also { it.value = this }
fun <T, R> LiveData<T>.map(func: (T) -> R): LiveData<R> = Transformations.map(this, func)
fun <T, R> LiveData<T>.switchMap(func: (T) -> LiveData<R>): LiveData<R> = Transformations.switchMap(this, func)
fun <T, R : Any?> LiveData<T>.switchMapNullable(func: (T) -> LiveData<R>?): LiveData<R> {
    val result = MediatorLiveData<R>()
    var source: LiveData<R>? = null
    result.addSource(this) {
        val newLiveData = func(it)
                ?: MutableLiveData<R>().apply { value = null }
        if (source == newLiveData)
            return@addSource

        source?.also { result.removeSource<R>(it) }
        source = newLiveData
        source?.also {
            result.addSource(it) { result.value = it }
        }
    }
    return result
}

inline fun <reified T1, reified T2> LiveData<T1>.combineLatest(other: LiveData<T2>): LiveData<Pair<T1, T2>> {
    val result = object : MediatorLiveData<Pair<T1, T2>>() {
        var v1: T1? = null
        var v1Set = false
        var v2: T2? = null
        var v2Set = false

        @Suppress("NAME_SHADOWING")
        fun update() {
            if (!v1Set || !v2Set)
                return
            value = v1 as T1 to v2 as T2
        }
    }

    result.addSource(this) {
        result.v1 = it
        result.v1Set = true
        result.update()
    }
    result.addSource(other) {
        result.v2 = it
        result.v2Set = true
        result.update()
    }
    return result
}

inline fun <reified T1, reified T2, reified T3> LiveData<T1>.combineLatest(
    other1: LiveData<T2>,
    other2: LiveData<T3>
): LiveData<Triple<T1, T2, T3>> {
    val result = object : MediatorLiveData<Triple<T1, T2, T3>>() {
        var v1: T1? = null
        var v1Set = false
        var v2: T2? = null
        var v2Set = false
        var v3: T3? = null
        var v3Set = false

        @Suppress("NAME_SHADOWING")
        fun update() {
            if (!v1Set || !v2Set || !v3Set)
                return
            value = Triple(v1 as T1, v2 as T2, v3 as T3)
        }
    }

    result.addSource(this) {
        result.v1 = it
        result.v1Set = true
        result.update()
    }
    result.addSource(other1) {
        result.v2 = it
        result.v2Set = true
        result.update()
    }
    result.addSource(other2) {
        result.v3 = it
        result.v3Set = true
        result.update()
    }
    return result
}

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

fun Drawable.setTintCompat(@ColorInt tint: Int) {
    DrawableCompat.setTint(this, tint)
}
