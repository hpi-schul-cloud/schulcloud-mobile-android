@file:Suppress("TooManyFunctions")

package org.schulcloud.mobile.utils

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.Request
import org.schulcloud.mobile.SchulCloudApp
import org.schulcloud.mobile.network.FeathersResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object NetworkUtil {
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

fun <T> Call<List<T>>.toFeatherResponse(): Call<FeathersResponse<List<T>>> {
    return MapCall(this) { list ->
        list?.run {
            FeathersResponse<List<T>>().also {
                it.total = size
                it.limit = size
                it.skip = 0
                it.data = this
            }
        }
    }
}

class MapCall<T, R>(private val toMap: Call<T>, private val mapper: (T?) -> R?) : Call<R> {
    private fun mapResponse(response: Response<T>): Response<R> {
        return if (response.isSuccessful)
            Response.success(mapper(response.body()), response.raw())
        else Response.error(response.errorBody()!!, response.raw())
    }

    override fun enqueue(callback: Callback<R>?) {
        toMap.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>?, response: Response<T>) {
                callback?.onResponse(this@MapCall, mapResponse(response))
            }

            override fun onFailure(call: Call<T>?, t: Throwable) {
                callback?.onFailure(this@MapCall, t)
            }
        })
    }

    override fun isExecuted() = toMap.isExecuted
    override fun clone() = MapCall(toMap.clone(), mapper)
    override fun isCanceled() = toMap.isCanceled
    override fun cancel() = toMap.cancel()
    override fun execute() = mapResponse(toMap.execute())
    override fun request(): Request = toMap.request()
}
