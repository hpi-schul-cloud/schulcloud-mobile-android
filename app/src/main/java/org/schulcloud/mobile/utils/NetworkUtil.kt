package org.schulcloud.mobile.utils

import android.content.Context
import android.net.ConnectivityManager
import org.schulcloud.mobile.SchulCloudApp

class NetworkUtil {

    companion object {

        val TYPE_NOT_CONNECTED = 0
        val TYPE_WIFI = 1
        val TYPE_MOBILE = 2

        fun isOnline(): Boolean {
            val context = SchulCloudApp.instance
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }

        fun getConnectivityStatus(): Int {
            val context = SchulCloudApp.instance
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    ?: return TYPE_NOT_CONNECTED

            val netInfo = cm.activeNetworkInfo
            if (netInfo != null) {
                if (netInfo.type == ConnectivityManager.TYPE_WIFI)
                    return TYPE_WIFI

                if (netInfo.type == ConnectivityManager.TYPE_MOBILE)
                    return TYPE_MOBILE
            }
            return TYPE_NOT_CONNECTED
        }

    }

}