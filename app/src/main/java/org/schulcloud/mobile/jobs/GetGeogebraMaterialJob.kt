package org.schulcloud.mobile.jobs

import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.content.GeogebraMaterial
import org.schulcloud.mobile.models.content.GeogebraResponse
import org.schulcloud.mobile.utils.MIME_APPLICATION_JSON
import org.schulcloud.mobile.utils.loge
import org.schulcloud.mobile.utils.logi

class GetGeogebraMaterialJob(private val materialId: String, callback: RequestJobCallback) : RequestJob(callback) {
    companion object {
        val TAG: String = GetGeogebraMaterialJob::class.java.simpleName

        private const val GEOGEBRA_API = "http://www.geogebra.org/api/json.php"
        // language=json
        private const val GEOGEBRA_REQUEST = """{ "request": {
  "-api": "1.0.0",
  "task": {
    "-type": "fetch",
    "fields": {
      "field": [
        { "-name": "preview_url" }
      ]
    },
    "filters" : {
      "field": [
        { "-name":"id", "#text":"%s" }
      ]
    },
    "limit": { "-num": "1" }
  }
}
}"""

        private val HTTP_CLIENT = OkHttpClient()
        private val GSON = GsonBuilder().create()
    }

    override suspend fun onRun() {
        val response = HTTP_CLIENT.newCall(Request.Builder().url(GEOGEBRA_API)
                .post(RequestBody.create(MediaType.parse(MIME_APPLICATION_JSON),
                        GEOGEBRA_REQUEST.format(materialId)))
                .build()).execute()
        if (response.body() == null) {
            loge(TAG, "Error while fetching preview url for Geogebra material $materialId")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
            return
        }

        val parsed = GSON.fromJson(response.body()?.charStream(), GeogebraResponse::class.java)
        if (parsed?.responses?.response?.item?.previewUrl == null) {
            loge(TAG, "Error while parsing preview url for Geogebra material $materialId")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
            return
        }

        val material = GeogebraMaterial().apply {
            id = materialId
            previewUrl = parsed.responses?.response?.item?.previewUrl
        }
        logi(TAG, "Preview url for Geogebra material $materialId received")
        Sync.Data.with(GeogebraMaterial::class.java, listOf(material))
                .run()
    }
}
