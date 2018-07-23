package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.user.Account
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class PatchAccountJob(private val account: Account, callback: RequestJobCallback): RequestJob() {
    companion object {
        val TAG: String = GetDeviceJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().patchAccount(account.id,account).awaitResponse()

        if (response.isSuccessful) {
            if (BuildConfig.DEBUG) Log.i(TAG, "Account ${account.id} patched!")
            Sync.SingleData.with(Account::class.java,response.body()!!).run()
        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while patching account ${account.id}")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}