package org.schulcloud.mobile.jobs.base

import android.util.Log
import io.mockk.*
import io.realm.RealmQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import org.schulcloud.mobile.commonTest.courseList
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.network.ApiService
import org.schulcloud.mobile.network.ApiServiceInterface
import org.schulcloud.mobile.network.FeathersResponse
import org.schulcloud.mobile.commonTest.course
import org.schulcloud.mobile.utils.NetworkUtil
import retrofit2.Response

//@RunWith(RobolectricTestRunner::class)
//@Config(application = SchulCloudTestApp::class)
class RequestJobTest {
    val mockApiService = mockk<ApiServiceInterface>()
    val courses = courseList(3)
    val course = course("id")
    val toDelete: (RealmQuery<Course>.() -> RealmQuery<Course>) = { this }
    val callback = spyk<RequestJobCallback>()
    val mockSyncData = mockk<Sync.Data<Course>>()
    val call = mockk<(ApiServiceInterface.() -> Call<FeathersResponse<List<Course>>>)>()
    val feathersResponse = FeathersResponse<List<Course>>().apply {
        data = courses
    }
    val mockResponse = mockk<Response<FeathersResponse<List<Course>>>>()
    val feathersResponseWithoutData = FeathersResponse<List<Course>>()
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        mockkObject(ApiService, NetworkUtil, Sync.Data)
        mockkStatic(UserRepository::class)
        mockkStatic(android.util.Log::class)
        every { Log.w(ofType<String>(), ofType<String>()) } returns 0
        every { Log.w(ofType<String>(), ofType<String>(), any()) } returns 0
        every { Log.i(ofType<String>(), ofType<String>()) } returns 0

        every { ApiService.getInstance() } returns mockApiService
        every { NetworkUtil.isOnline() } returns true
        every { UserRepository.isAuthorized } returns true
        every { Sync.Data.with(Course::class.java, courses, toDelete) } returns mockSyncData
        every { mockSyncData.run() } just runs
        every { call(mockApiService)} returns mockk{
            every { execute() } returns mockResponse
            //coEvery { awaitResponse() } returns  mockResponse
        }
        every { mockResponse.body() } returns feathersResponse
        every { mockResponse.isSuccessful } returns true
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
        clearAllMocks()
    }

    @Test
    fun shouldCallApiServiceWhenOnlineAndNoAuthRequired(){
        runBlocking {
            RequestJob.Data.with(call, toDelete, callback).run()
        }
        verify { mockApiService.call() }
    }

    @Test
    fun shouldCallNoAuthErrorWhenAuthRequiredAndNotAuthorized() {
        every { UserRepository.isAuthorized } returns false
        runBlocking {
            RequestJob.Data.with(call, toDelete, callback, RequestJob.Precondition.AUTH).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.NO_AUTH) }
    }

    @Test
    fun shouldCallApiServiceWhenAuthRequiredAndAuthorized() {
        every { UserRepository.isAuthorized } returns true
        runBlocking {
            RequestJob.Data.with(call, toDelete, callback, RequestJob.Precondition.AUTH).run()
        }
        verify { mockApiService.call() }
    }

    @Test
    fun shouldCallApiServiceWhenAuthNotRequiredAndNotAuthorized() {
        every { UserRepository.isAuthorized } returns false
        runBlocking {
            RequestJob.Data.with(call, toDelete, callback).run()
        }
        verify { mockApiService.call() }
    }

    @Test
    fun shouldCallNoNetworkErrorWhenNotOnline(){
        every { NetworkUtil.isOnline() } returns false
        runBlocking {
            RequestJob.Data.with(call, toDelete, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.NO_NETWORK) }
    }

    //TODO: fix those tests
    @Test
    fun shouldCallSyncWhenApiServiceCallSuccessfulAndDataNotNull(){
        every { mockResponse.body() } returns feathersResponse
        every { mockResponse.isSuccessful } returns true
        runBlocking {
            RequestJob.Data.with(call, toDelete, callback).run()
        }
        //verify { callback.error(RequestJobCallback.ErrorCode.ERROR) }
       verify { Sync.Data.with(Course::class.java, courses, toDelete) }
    }

    @Test
    fun shouldCallErrorAndNotCallSyncWhenApiServiceCallNotSuccessful(){
        every { mockResponse.isSuccessful } returns false
        runBlocking {
            RequestJob.Data.with(call, toDelete, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.ERROR) }
        verify { mockSyncData wasNot Called }
    }

    @Test
    fun shouldCallErrorAndNotCallSyncWhenDataNull(){
        every { mockResponse.body() } returns feathersResponseWithoutData
        runBlocking {
            RequestJob.Data.with(call, toDelete, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.ERROR) }
        verify { mockSyncData wasNot Called }
    }
}
