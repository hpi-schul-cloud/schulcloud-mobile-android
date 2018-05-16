package org.schulcloud.mobile.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.schulcloud.mobile.config.Config
import org.schulcloud.mobile.models.user.UserRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {

    private var service: ApiServiceInterface? = null

    @Synchronized
    fun getInstance(): ApiServiceInterface {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC

        val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val builder = original.newBuilder()
                    if (UserRepository.isAuthorized) {
                        builder.header(Config.HEADER_AUTH, Config.HEADER_AUTH_VALUE_PREFIX + UserRepository.token)
                    }
                    chain.proceed(builder.build())
                }
                .addInterceptor(loggingInterceptor)
                .build()

        val gson = GsonBuilder()
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl(Config.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        service = retrofit.create(ApiServiceInterface::class.java)
        return service as ApiServiceInterface
    }

}