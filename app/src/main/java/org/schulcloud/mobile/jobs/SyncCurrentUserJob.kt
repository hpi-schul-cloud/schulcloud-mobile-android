package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.network.ApiService

class SyncCurrentUserJob(callback: RequestJobCallback): RequestJob(callback){
    companion object {
        val TAG: String = SyncCurrentUserJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance()..awaitResponse()
        if (response.isSuccessful) {
            Log.i(TAG, "Current user synced")

            // Sync
            Sync.Data.with(User::class.java, response.body()!!.data!!)
                    .run()
        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while syncing current user)
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}
