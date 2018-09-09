package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.network.ApiService
import org.schulcloud.mobile.storages.UserStorage
import ru.gildor.coroutines.retrofit.awaitResponse

class SyncCurrentUserJob(private val userId: String,callback: RequestJobCallback): RequestJob(callback){
    companion object {
        val TAG: String = SyncCurrentUserJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().getUser(userId).awaitResponse()
        if (response.isSuccessful) {
            Log.i(TAG, "Current user $userId synced")

            // Sync
            Sync.SingleData.with(User::class.java, response.body()!!)
                    .run()

            var roles: Array<String?> = arrayOf()
            response!!.body()?.roles?.forEach {
                roles.plus(it)
            }
            UserStorage().roles = roles
        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while syncing current user!")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}
