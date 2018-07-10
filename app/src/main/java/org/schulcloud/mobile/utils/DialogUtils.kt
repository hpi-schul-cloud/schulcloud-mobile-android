package org.schulcloud.mobile.utils

import android.content.Context
import android.support.annotation.StringRes
import android.widget.Toast
import org.schulcloud.mobile.R

/**
 * Date: 7/10/2018
 */
fun Context.showGenericError(@StringRes messageRes: Int): Toast = showGenericError(getString(messageRes))
fun Context.showGenericError(message: String): Toast {
    return Toast.makeText(this, getString(R.string.dialog_error_format, message), Toast.LENGTH_SHORT)
            .apply { show() }
}
