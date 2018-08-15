package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.event.Event
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class ListEventsJob(callback: RequestJobCallback) : RequestJob(callback) {
    companion object {
        val TAG: String = ListEventsJob::class.java.simpleName
    }

    override suspend fun onRun() {

        val response = ApiService.getInstance().listEvents().awaitResponse()
        if (response.isSuccessful) {

            if (BuildConfig.DEBUG) Log.i(TAG, "Courses received")

            // Sync
            Sync.Data.with(Event::class.java, response.body()!!)
                    .run()

        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while fetching courses list")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}
