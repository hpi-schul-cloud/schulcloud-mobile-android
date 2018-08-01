package org.schulcloud.mobile.jobs

import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Credentials
import org.schulcloud.mobile.network.ApiService
import org.schulcloud.mobile.storages.UserStorage
import org.schulcloud.mobile.utils.JWTUtil
import org.schulcloud.mobile.utils.logi
import org.schulcloud.mobile.utils.logw
import ru.gildor.coroutines.retrofit.awaitResponse

class CreateAccessTokenJob(private val credentials: Credentials, callback: RequestJobCallback) : RequestJob(callback) {

    companion object {
        val TAG: String = CreateAccessTokenJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().createToken(credentials).awaitResponse()
        val token = response.body()

        if (response.isSuccessful && token != null) {
            logi(TAG, "AccessToken created")

            val userStorage = UserStorage()
            userStorage.accessToken = token.accessToken!!
            userStorage.userId = JWTUtil().decodeToCurrentUser(token.accessToken!!)!!

            callback?.success()
        } else {
            logw(TAG, "AccessToken not created")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}
