package org.schulcloud.mobile.commonTest.rules

import android.util.Log
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.network.ApiService
import org.schulcloud.mobile.network.ApiServiceInterface
import org.schulcloud.mobile.utils.NetworkUtil
import retrofit2.Response

class RequestJobTestRule<T>(private val mockApiService: ApiServiceInterface,
                            private val responseBody: T,
                            private val mockResponse: Response<T>) : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                mockkObject(ApiService, NetworkUtil)
                mockkStatic(UserRepository::class, android.util.Log::class)
                mockkStatic("ru.gildor.coroutines.retrofit.CallAwaitKt")

                every { Log.w(any(), ofType<String>()) } returns 0
                every { Log.w(any(), any(), any()) } returns 0
                every { Log.i(any(), any()) } returns 0
                every { Log.e(any(), any()) } returns 0

                every { ApiService.getInstance() } returns mockApiService
                every { NetworkUtil.isOnline() } returns true
                every { UserRepository.isAuthorized } returns true


                every { mockResponse.body() } returns responseBody
                every { mockResponse.isSuccessful } returns true

                base.evaluate()

                clearAllMocks()
            }
        }
    }
}
