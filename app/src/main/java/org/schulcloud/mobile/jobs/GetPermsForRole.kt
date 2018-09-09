package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.user.UserPermissions
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class GetPermsForRole(private val roleId: String,private val userId: String, callback: RequestJobCallback) : RequestJob(callback) {
    companion object {
        val TAG: String = GetPermsForRole::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().getPermsForRole(roleId).awaitResponse()

        if (response.isSuccessful) {
            if (BuildConfig.DEBUG) Log.i(TAG, "Permissions for role $roleId received")

            // Sync
            var perms: UserPermissions = UserPermissions()
            perms.userId = userId
            response.body()!!.forEach{
                perms.permissions.plus(it)
            }
            Sync.SingleData.with()
        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while fetching permissions for role $roleId")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}
