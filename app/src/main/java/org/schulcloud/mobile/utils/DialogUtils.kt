package org.schulcloud.mobile.utils

import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import org.schulcloud.mobile.R


fun Context.showGenericError(@StringRes messageRes: Int): Toast = showGenericError(getString(messageRes))
fun Context.showGenericError(message: String): Toast {
    return Toast.makeText(this, getString(R.string.dialog_error_format, message), Toast.LENGTH_SHORT)
            .apply { show() }
}

fun Context.showGenericNeutral(@StringRes messageRes: Int): Toast = showGenericNeutral(getString(messageRes))
fun Context.showGenericNeutral(message: String): Toast {
    return Toast.makeText(this, message, Toast.LENGTH_SHORT)
            .apply { show() }
}

fun Context.showGenericSuccess(@StringRes messageRes: Int): Toast = showGenericSuccess(getString(messageRes))
fun Context.showGenericSuccess(message: String): Toast {
    return Toast.makeText(this, message, Toast.LENGTH_SHORT)
            .apply { show() }
}

suspend fun <T> Context.withProgressDialog(@StringRes messageRes: Int, block: suspend () -> T): T {
    return withProgressDialog(getString(messageRes), block)
}

suspend fun <T> Context.withProgressDialog(message: String, block: suspend () -> T): T {
    val dialog = ProgressDialog(this).apply {
        setMessage(message)
        show()
    }
    val res = block()
    dialog.dismiss()
    return res
}
