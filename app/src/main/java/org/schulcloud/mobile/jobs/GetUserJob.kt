package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class GetUserJob(private val userId: String, callback: RequestJobCallback) : RequestJob(callback) {
    companion object {
        val TAG: String = GetCourseJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().getUser(userId).awaitResponse()

        if (response.isSuccessful) {
            if (BuildConfig.DEBUG) Log.i(TAG, "User $userId received")

            // Sync
            Sync.SingleData.with(User::class.java, response.body()!!).run()
        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while fetching user $userId")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}
