package org.schulcloud.mobile.jobs

import android.util.Log
import com.google.gson.Gson
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class PatchUserJob(private val user: User, callback: RequestJobCallback): RequestJob(callback){
    companion object {
        val TAG = PatchUserJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().patchUser(user.id,user).awaitResponse()
        var newUser = Gson().fromJson<User>(response.body()!!,User::class.java)
        if(newUser.gender == null)
            newUser.gender = "null"

        if (response.isSuccessful) {
            if (BuildConfig.DEBUG) Log.i(TAG, "User $user.id patched!")
            Sync.SingleData.with(User::class.java,newUser)
        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while patching user $user.id")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }

}