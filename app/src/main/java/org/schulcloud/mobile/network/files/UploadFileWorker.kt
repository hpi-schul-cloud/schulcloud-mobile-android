package org.schulcloud.mobile.network.files

import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.schulcloud.mobile.models.file.File
import retrofit2.Response

class UploadFileWorker(file: File, requestBody: RequestBody): FileService.BaseWorker() {
    override val type = "upload"

    override suspend fun execute(): Response<ResponseBody>? {

        return null
    }

    override fun cancel(){
        super.cancel()
        FileService.workersUpload.remove(this)
    }
}
