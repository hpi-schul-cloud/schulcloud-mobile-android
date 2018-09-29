package org.schulcloud.mobile.worker

import androidx.work.Data
import java.util.*

class WorkerInfo(id: UUID,inputData: Data) {
    val id: UUID = id
    val inputData: Data = inputData
}
