package org.schulcloud.mobile.utils

import android.util.Base64
import com.google.gson.JsonParser
import java.nio.charset.Charset
import java.io.UnsupportedEncodingException

class JWTUtil {

    private val KEY_USER_ID = "userId"

    fun decodeToCurrentUser(JWTEncoded: String): String? {
        try {
            val split = JWTEncoded.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            // get body of jwt
            val jsonParser = JsonParser()
            val bodyJson = getJson(split[1])
            val jsonObject = jsonParser.parse(bodyJson).getAsJsonObject()

            return jsonObject.get(KEY_USER_ID).getAsString()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            return null
        }

    }

    @Throws(UnsupportedEncodingException::class)
    private fun getJson(strEncoded: String): String {
        val decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE)
        return String(decodedBytes, Charset.forName("UTF-8"))
    }

}