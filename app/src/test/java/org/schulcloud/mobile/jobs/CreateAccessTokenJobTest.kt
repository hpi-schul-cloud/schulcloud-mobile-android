package org.schulcloud.mobile.jobs

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import io.mockk.*
import io.realm.RealmQuery
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.schulcloud.mobile.SchulCloudTestApp
import org.schulcloud.mobile.commonTest.rules.CoroutinesRule
import org.schulcloud.mobile.commonTest.rules.RequestJobTestRule
import org.schulcloud.mobile.commonTest.rules.SharedPreferencesRule
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.AccessToken
import org.schulcloud.mobile.models.Credentials
import org.schulcloud.mobile.network.ApiServiceInterface
import org.schulcloud.mobile.storages.UserStorage
import org.schulcloud.mobile.utils.JwtUtil
import org.schulcloud.mobile.utils.NetworkUtil
import retrofit2.Response
import ru.gildor.coroutines.retrofit.awaitResponse

@RunWith(RobolectricTestRunner::class)
@Config(application = SchulCloudTestApp::class)
class CreateAccessTokenJobTest {
    private val mockApiService = mockk<ApiServiceInterface>()
    private val accessTokenText = "accessToken"
    private val userId = "userId"
    private val token = AccessToken().also {
        it.accessToken = accessTokenText
    }
    private val emptyToken = AccessToken()
    private val callback = spyk<RequestJobCallback>()
    private val credentials = Credentials("user", "password")
    private val mockResponse = mockk<Response<AccessToken>>()
    private val throwable = Throwable()
    private val timeoutThrowable = Throwable("connect timed out")

    // necessary because mockkObject calls the init block of the object, in this case of UserStorage
    private val userStorageName = "pref_user_v2"
    private val sharedPreferences = ApplicationProvider.getApplicationContext<SchulCloudTestApp>()
            .getSharedPreferences("test_preferences", Context.MODE_PRIVATE)

    @Rule
    @JvmField
    val sharedPreferencesRule = SharedPreferencesRule(userStorageName, sharedPreferences)

    @Rule
    @JvmField
    val requestJobTestRule = RequestJobTestRule(mockApiService, token, mockResponse)

    @Before
    fun setUp() {
        mockkObject(UserStorage, JwtUtil)
        every { UserStorage.accessToken = any() } just runs
        every { UserStorage.userId = any() } just runs
        every { JwtUtil.decodeToCurrentUser(accessTokenText) } returns userId

        every { mockApiService.createToken(credentials) } returns mockk {
            coEvery { awaitResponse() } returns mockResponse
        }
    }

    @Test
    fun shouldCallApiServiceWhenOnline() {
        runBlocking {
            CreateAccessTokenJob(credentials, callback).run()
        }
        verify { mockApiService.createToken(credentials) }
    }

    @Test
    fun shouldCallNoNetworkErrorWhenNotOnline() {
        every { NetworkUtil.isOnline() } returns false
        runBlocking {
            CreateAccessTokenJob(credentials, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.NO_NETWORK) }
        verify(inverse = true) { mockApiService.createToken(credentials) }
    }

    @Test
    fun shouldSaveUserDataAndCallSuccessWhenApiServiceCallSuccessfulAndTokenNotNull() {
        runBlocking {
            CreateAccessTokenJob(credentials, callback).run()
        }
        verify {
            UserStorage.accessToken = token.accessToken
            UserStorage.userId = userId
            callback.success()
        }
    }

    @Test
    fun shouldCallErrorAndNotSaveDataWhenApiServiceCallNotSuccessful() {
        every { mockResponse.isSuccessful } returns false
        runBlocking {
            CreateAccessTokenJob(credentials, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.ERROR) }
        verify(inverse = true) {
            UserStorage.accessToken = token.accessToken
            UserStorage.userId = userId
            callback.success()
        }
    }

    @Test
    fun shouldCallErrorAndNotSaveDataWhenAccessTokenNull() {
        every { mockResponse.body() } returns emptyToken
        runBlocking {
            CreateAccessTokenJob(credentials, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.ERROR) }
        verify(inverse = true) {
            UserStorage.accessToken = token.accessToken
            UserStorage.userId = userId
            callback.success()
        }
    }

    @Test
    fun shouldCallErrorAndNotCallSyncWhenJobFails() {
        every { mockApiService.createToken(credentials) } throws throwable
        runBlocking {
            CreateAccessTokenJob(credentials, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.ERROR) }
        verify(inverse = true) {
            UserStorage.accessToken = token.accessToken
            UserStorage.userId = userId
            callback.success()
        }
    }

    @Test
    fun shouldCallTimeoutErrorAndNotCallSyncWhenJobFailsWithTimeout() {
        every { mockApiService.createToken(credentials) } throws timeoutThrowable
        runBlocking {
            CreateAccessTokenJob(credentials, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.TIMEOUT) }
        verify(inverse = true) {
            UserStorage.accessToken = token.accessToken
            UserStorage.userId = userId
            callback.success()
        }
    }
}
