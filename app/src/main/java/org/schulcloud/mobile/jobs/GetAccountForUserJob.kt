package org.schulcloud.mobile.jobs

import org.schulcloud.mobile.models.user.Account
import android.util.Log
import com.google.gson.Gson
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class GetAccountForUserJob(private val userId: String, callback: RequestJobCallback): RequestJob(callback) {
    companion object {
        val TAG = GetAccountForUserJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().getAccountForUser(userId).awaitResponse()

        if (response.isSuccessful) {
            if (BuildConfig.DEBUG) Log.i(TAG, "Account for user $userId received")
            var body = response.body().toString().substring(1,response.body().toString().length - 1)

            // Sync
            val gson = Gson()
            val account = gson.fromJson(body,Account::class.java)
            Sync.SingleData.with(Account::class.java, account).run()
            callback?.success()
        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Unable to fetch account for user $userId")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}