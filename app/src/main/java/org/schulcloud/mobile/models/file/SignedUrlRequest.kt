package org.schulcloud.mobile.models.file


/**
 * Date: 7/5/2018
 */
class SignedUrlRequest {
    companion object {
        val ACTION_GET = "getObject"
        val ACTION_PUT = "putObject"
    }

    var parent: String? = null
    var filename: String? = null
    var fileType: String? = null
}
