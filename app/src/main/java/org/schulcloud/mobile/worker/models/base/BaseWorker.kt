package org.schulcloud.mobile.worker.models.base

import android.content.Context
import androidx.core.util.Pair
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import org.schulcloud.mobile.worker.WorkerService
import java.util.*

abstract class BaseWorker(context: Context, params: WorkerParameters): Worker(context,params){
    private var mUUID: UUID = UUID.randomUUID()

    override fun onStartWork(): ListenableFuture<Pair<Result, Data>> {
        WorkerService.addWorker(WorkerService.WorkerInfo(mUUID,inputData))
        return super.onStartWork()
    }

    override fun onStopped(cancelled: Boolean) {
        WorkerService.workerFinished(mUUID)
    }
}
