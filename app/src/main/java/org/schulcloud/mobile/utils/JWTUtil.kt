@file:Suppress("TooManyFunctions")
package org.schulcloud.mobile.utils

import android.util.Base64
import com.google.gson.JsonParser
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

class JWTUtil {
    companion object {
        private const val KEY_USER_ID = "userId"
    }

    fun decodeToCurrentUser(JWTEncoded: String): String? {
        return try {
            val split = JWTEncoded.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            // get body of jwt
            val jsonParser = JsonParser()
            val bodyJson = getJson(split[1])
            val jsonObject = jsonParser.parse(bodyJson).getAsJsonObject()

            jsonObject.get(KEY_USER_ID).asString
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            null
        }
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getJson(strEncoded: String): String {
        val decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE)
        return String(decodedBytes, Charset.forName("UTF-8"))
    }
}
