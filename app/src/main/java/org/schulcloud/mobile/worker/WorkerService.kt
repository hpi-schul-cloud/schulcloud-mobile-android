package org.schulcloud.mobile.worker

import org.schulcloud.mobile.worker.models.base.BaseWorker
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

object WorkerService {
    private var workerCount: AtomicInteger = AtomicInteger()
    private var workers: MutableList<BaseWorker<Any,Any>> = mutableListOf()


    fun cancelAll(){
        workers.forEach {
            it.cancel()
        }
        workers.clear()
    }

    fun <R,S> enqueWorker(worker: BaseWorker<R,S>){
        workers.plus(worker)
    }

    fun cancelWorker(id: UUID){
        workers.forEach {
            if(it.mID == id)
                it.cancel()
        }
    }
}
