package org.schulcloud.mobile.jobs

import org.schulcloud.mobile.models.user.Account
import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class GetAccountForUserJob(private val userId: String, callback: RequestJobCallback): RequestJob(callback) {
    companion object {
        val TAG = GetAccountForUserJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().getAccountForUser(userId).awaitResponse()

        if (response.isSuccessful) {
            if (BuildConfig.DEBUG) Log.i(GetAccountForUserJob.TAG, "Account for user $userId received")

            // Sync
            Sync.SingleData.with(Account::class.java, response.body()!!).run()
            callback?.success()
        } else {
            if (BuildConfig.DEBUG) Log.e(GetAccountForUserJob.TAG, "Error while fetching account for user $userId")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}