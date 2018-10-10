package org.schulcloud.mobile.worker.models.base

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.util.*

open class BaseWorker<R,S>(parameter: R,action:(R) -> S, callback:(S) -> Void){
    val mID = UUID.randomUUID()
    protected var mAction = action
    protected var mCallback = callback
    protected var mActionDeferred: Deferred<Void>? = null
    open var mParameter: R = parameter
    var isExecuted: Boolean = false

    fun execute(){
        mActionDeferred = async {
            val output = mAction(mParameter)
            mCallback(output)
        }
    }

    open fun cancel(){
        if(mActionDeferred != null)
            mActionDeferred?.cancel(Throwable("This worker has benn canceled!"))
    }
}
