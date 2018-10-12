package org.schulcloud.mobile.network.files

import okhttp3.ResponseBody
import retrofit2.Response

class UploadFileWorker: FileService.BaseWorker() {
    override suspend fun execute(): Response<ResponseBody>? {
        return null
    }
}
