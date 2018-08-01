@file:Suppress("TooManyFunctions")
package org.schulcloud.mobile.utils

import android.content.Context
import android.net.ConnectivityManager
import org.schulcloud.mobile.SchulCloudApp

class NetworkUtil {

    companion object {
        const val TYPE_NOT_CONNECTED = 0
        const val TYPE_WIFI = 1
        const val TYPE_MOBILE = 2

        fun isOnline(): Boolean {
            val context = SchulCloudApp.instance
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            return cm?.activeNetworkInfo?.isConnected ?: false
        }

        fun getConnectivityStatus(): Int {
            val context = SchulCloudApp.instance
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

            return when (cm?.activeNetworkInfo?.type) {
                ConnectivityManager.TYPE_WIFI -> TYPE_WIFI
                ConnectivityManager.TYPE_MOBILE -> TYPE_MOBILE
                else -> TYPE_NOT_CONNECTED
            }
        }
    }
}
