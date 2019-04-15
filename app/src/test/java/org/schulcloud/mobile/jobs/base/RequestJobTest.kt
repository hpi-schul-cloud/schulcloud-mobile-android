package org.schulcloud.mobile.jobs.base

import io.mockk.*
import io.realm.RealmQuery
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.schulcloud.mobile.commonTest.TestIdRealmObject
import org.schulcloud.mobile.commonTest.rules.CoroutinesRule
import org.schulcloud.mobile.commonTest.rules.RequestJobTestRule
import org.schulcloud.mobile.commonTest.testIdRealmObjectList
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.network.ApiServiceInterface
import org.schulcloud.mobile.network.FeathersResponse
import org.schulcloud.mobile.utils.NetworkUtil
import retrofit2.Call
import retrofit2.Response
import ru.gildor.coroutines.retrofit.awaitResponse

class RequestJobTest {
    private val mockApiService = mockk<ApiServiceInterface>()
    private val idRealmObjects = testIdRealmObjectList(3)
    private val toDelete: (RealmQuery<TestIdRealmObject>.() -> RealmQuery<TestIdRealmObject>) = { this }
    private val callback = spyk<RequestJobCallback>()
    private val apiServiceCall = mockk<ApiServiceInterface.() -> Call<FeathersResponse<List<TestIdRealmObject>>>>()
    private val feathersResponse = FeathersResponse<List<TestIdRealmObject>>().apply {
        data = idRealmObjects
    }
    private val throwable = Throwable()
    private val timeoutThrowable = Throwable("connect timed out")
    private val mockResponse = mockk<Response<FeathersResponse<List<TestIdRealmObject>>>>()
    private val feathersResponseWithoutData = FeathersResponse<List<TestIdRealmObject>>()
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Rule
    @JvmField
    val coroutinesRule = CoroutinesRule(mainThreadSurrogate)

    @Rule
    @JvmField
    val requestJobTestRule = RequestJobTestRule(mockApiService, feathersResponse, mockResponse)

    @Before
    fun setUp() {
        mockkObject(Sync.Data)
        every { Sync.Data.with(TestIdRealmObject::class.java, idRealmObjects, toDelete).run() } just runs
        every { mockApiService.apiServiceCall() } returns mockk {
            coEvery { awaitResponse() } returns mockResponse
        }
    }

    @Test
    fun shouldCallApiServiceWhenOnlineAndNoAuthRequired() {
        runBlocking {
            RequestJob.Data.with(apiServiceCall, toDelete, callback).run()
        }
        verify { mockApiService.apiServiceCall() }
    }

    @Test
    fun shouldCallNoAuthErrorWhenAuthRequiredAndNotAuthorized() {
        every { UserRepository.isAuthorized } returns false
        runBlocking {
            RequestJob.Data.with(apiServiceCall, toDelete, callback, RequestJob.Precondition.AUTH).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.NO_AUTH) }
        verify(inverse = true) { mockApiService.apiServiceCall() }
    }

    @Test
    fun shouldCallApiServiceWhenAuthRequiredAndAuthorized() {
        every { UserRepository.isAuthorized } returns true
        runBlocking {
            RequestJob.Data.with(apiServiceCall, toDelete, callback, RequestJob.Precondition.AUTH).run()
        }
        verify { mockApiService.apiServiceCall() }
    }

    @Test
    fun shouldCallApiServiceWhenAuthNotRequiredAndNotAuthorized() {
        every { UserRepository.isAuthorized } returns false
        runBlocking {
            RequestJob.Data.with(apiServiceCall, toDelete, callback).run()
        }
        verify { mockApiService.apiServiceCall() }
    }

    @Test
    fun shouldCallNoNetworkErrorWhenNotOnline() {
        every { NetworkUtil.isOnline() } returns false
        runBlocking {
            RequestJob.Data.with(apiServiceCall, toDelete, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.NO_NETWORK) }
        verify(inverse = true) { mockApiService.apiServiceCall() }
    }

    @Test
    fun shouldCallSyncWhenApiServiceCallSuccessfulAndDataNotNull() {
        runBlocking {
            RequestJob.Data.with(apiServiceCall, toDelete, callback).run()
        }
        verify { Sync.Data.with(TestIdRealmObject::class.java, idRealmObjects, toDelete).run() }
    }

    @Test
    fun shouldCallErrorAndNotCallSyncWhenApiServiceCallNotSuccessful() {
        every { mockResponse.isSuccessful } returns false
        runBlocking {
            RequestJob.Data.with(apiServiceCall, toDelete, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.ERROR) }
        verify(inverse = true) { Sync.Data.with(TestIdRealmObject::class.java, idRealmObjects, toDelete).run() }
    }

    @Test
    fun shouldCallErrorAndNotCallSyncWhenDataNull() {
        every { mockResponse.body() } returns feathersResponseWithoutData
        runBlocking {
            RequestJob.Data.with(apiServiceCall, toDelete, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.ERROR) }
        verify(inverse = true) { Sync.Data.with(TestIdRealmObject::class.java, idRealmObjects, toDelete).run() }
    }

    @Test
    fun shouldCallErrorAndNotCallSyncWhenJobFails() {
        every { mockApiService.apiServiceCall() } throws throwable
        runBlocking {
            RequestJob.Data.with(apiServiceCall, toDelete, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.ERROR) }
        verify(inverse = true) { Sync.Data.with(TestIdRealmObject::class.java, idRealmObjects, toDelete).run() }
    }

    @Test
    fun shouldCallTimeoutErrorAndNotCallSyncWhenJobFailsWithTimeout() {
        every { mockApiService.apiServiceCall() } throws timeoutThrowable
        runBlocking {
            RequestJob.Data.with(apiServiceCall, toDelete, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.TIMEOUT) }
        verify(inverse = true) { Sync.Data.with(TestIdRealmObject::class.java, idRealmObjects, toDelete).run() }
    }
}
