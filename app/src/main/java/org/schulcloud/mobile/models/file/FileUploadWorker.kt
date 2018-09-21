package org.schulcloud.mobile.models.file

import androidx.work.Worker

class FileUploadWorker: Worker() {

    override fun doWork(): Result {
        return Result.SUCCESS
    }

}
