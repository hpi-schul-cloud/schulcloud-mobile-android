package org.schulcloud.mobile.jobs.base

import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.utils.NetworkUtil

abstract class RequestJob(protected val callback: RequestJobCallback?, private vararg val preconditions: Precondition) {

    enum class Precondition {
        AUTH
    }

    fun run() {
        if (preconditions.contains(Precondition.AUTH) && !UserRepository.isAuthorized) {
            callback?.error(RequestJobCallback.ErrorCode.NO_AUTH)
            return
        }

        if (!NetworkUtil.isOnline()) {
            callback?.error(RequestJobCallback.ErrorCode.NO_NETWORK)
            return
        }

        launch {
            try {
                onRun()
            } catch (e: Throwable) {
                callback?.error(RequestJobCallback.ErrorCode.ERROR)
            }
        }
    }

    protected abstract suspend fun onRun()

}