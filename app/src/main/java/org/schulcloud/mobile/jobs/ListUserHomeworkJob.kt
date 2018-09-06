package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class ListUserHomeworkJob(callback: RequestJobCallback) : RequestJob(callback) {

    companion object {
        val TAG: String = ListUserHomeworkJob::class.java.simpleName
    }

    override suspend fun onRun() {

        val response = ApiService.getInstance().listUserHomework().awaitResponse()
        if (response.isSuccessful) {

            if (BuildConfig.DEBUG)
                Log.i(TAG, "Homework received")

            // Sync
            Sync.Data.with(Homework::class.java, response.body()!!.data!!)
                    .run()
        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while fetching homework list")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}
