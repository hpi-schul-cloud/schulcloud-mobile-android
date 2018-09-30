package org.schulcloud.mobile.network

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import io.realm.RealmList
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.config.Config
import org.schulcloud.mobile.models.Credentials
import org.schulcloud.mobile.models.base.RealmString
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.utils.HOST
import org.xml.sax.ErrorHandler
import org.xml.sax.SAXParseException
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.ResponseBody
import java.io.IOException


object ApiService {
    private var service: ApiServiceInterface? = null

    @Synchronized
    fun getInstance(): ApiServiceInterface {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
                .addInterceptor(object: Interceptor{
                    override fun intercept(chain: Interceptor.Chain?): Response {
                        val request = chain!!.request()
                        val builder = request.newBuilder()

                        if (UserRepository.isAuthorized
                                && request.url().host().equals(HOST.substringAfterLast('/'), true)) {
                            builder.header(Config.HEADER_AUTH, Config.HEADER_AUTH_VALUE_PREFIX + UserRepository.token)
                        }
                        return chain.proceed(builder.build()) as Response
                    }
                })
                .addInterceptor(loggingInterceptor)
                .build()

        val gson = GsonBuilder()
                .registerTypeAdapter((object : TypeToken<RealmList<RealmString>>() {}).type,
                        object : TypeAdapter<RealmList<RealmString>>() {
                            override fun read(reader: JsonReader?): RealmList<RealmString> {
                                if (reader == null)
                                    return RealmList()

                                val list = RealmList<RealmString>()
                                reader.beginArray()
                                while (reader.hasNext())
                                    list += RealmString(reader.nextString())
                                reader.endArray()
                                return list
                            }

                            override fun write(out: JsonWriter?, value: RealmList<RealmString>?) {
                                if (out == null || value == null)
                                    return

                                out.beginArray()
                                for (string in value)
                                    out.value(string.value)
                                out.endArray()
                            }
                        })
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        service = retrofit.create(ApiServiceInterface::class.java)
        return service as ApiServiceInterface
    }
}
