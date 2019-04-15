import io.mockk.*
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.schulcloud.mobile.commonTest.TestIdRealmObject
import org.schulcloud.mobile.commonTest.rules.CoroutinesRule
import org.schulcloud.mobile.commonTest.rules.RequestJobTestRule
import org.schulcloud.mobile.commonTest.testIdRealmObject
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.network.ApiServiceInterface
import org.schulcloud.mobile.utils.NetworkUtil
import retrofit2.Call
import retrofit2.Response
import ru.gildor.coroutines.retrofit.awaitResponse

class RequestJobSingleDataTest {
    private val mockApiService = mockk<ApiServiceInterface>()
    private val idRealmObject = testIdRealmObject("id")
    private val itemId = "itemId"
    private val callback = spyk<RequestJobCallback>()
    private val apiServiceCall = mockk<ApiServiceInterface.() -> Call<TestIdRealmObject>>()
    private val mockResponse = mockk<Response<TestIdRealmObject>>()
    private val throwable = Throwable()
    private val timeoutThrowable = Throwable("connect timed out")
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Rule
    @JvmField
    val coroutinesRule = CoroutinesRule(mainThreadSurrogate)

    @Rule
    @JvmField
    val requestJobTestRule = RequestJobTestRule(mockApiService, idRealmObject, mockResponse)

    @Before
    fun setUp() {
        mockkObject(Sync.SingleData)
        every { Sync.SingleData.with(TestIdRealmObject::class.java, idRealmObject, itemId).run() } just runs
        every { mockApiService.apiServiceCall() } returns mockk {
            coEvery { awaitResponse() } returns mockResponse
        }
    }

    @Test
    fun shouldCallApiServiceWhenOnlineAndNoAuthRequired() {
        runBlocking {
            RequestJob.SingleData.with(itemId, apiServiceCall, callback).run()
        }
        verify { mockApiService.apiServiceCall() }
    }

    @Test
    fun shouldCallNoAuthErrorWhenAuthRequiredAndNotAuthorized() {
        every { UserRepository.isAuthorized } returns false
        runBlocking {
            RequestJob.SingleData.with(itemId, apiServiceCall, callback, RequestJob.Precondition.AUTH).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.NO_AUTH) }
        verify(inverse = true) { mockApiService.apiServiceCall() }
    }

    @Test
    fun shouldCallApiServiceWhenAuthRequiredAndAuthorized() {
        every { UserRepository.isAuthorized } returns true
        runBlocking {
            RequestJob.SingleData.with(itemId, apiServiceCall, callback, RequestJob.Precondition.AUTH).run()
        }
        verify { mockApiService.apiServiceCall() }
    }

    @Test
    fun shouldCallApiServiceWhenAuthNotRequiredAndNotAuthorized() {
        every { UserRepository.isAuthorized } returns false
        runBlocking {
            RequestJob.SingleData.with(itemId, apiServiceCall, callback).run()
        }
        verify { mockApiService.apiServiceCall() }
    }

    @Test
    fun shouldCallNoNetworkErrorWhenNotOnline() {
        every { NetworkUtil.isOnline() } returns false
        runBlocking {
            RequestJob.SingleData.with(itemId, apiServiceCall, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.NO_NETWORK) }
        verify(inverse = true) { mockApiService.apiServiceCall() }
    }

    @Test
    fun shouldCallSyncWhenApiServiceCallSuccessful() {
        runBlocking {
            RequestJob.SingleData.with(itemId, apiServiceCall, callback).run()
        }
        verify {  Sync.SingleData.with(TestIdRealmObject::class.java, idRealmObject, itemId).run() }
    }

    @Test
    fun shouldCallErrorAndNotCallSyncWhenApiServiceCallNotSuccessful() {
        every { mockResponse.isSuccessful } returns false
        runBlocking {
            RequestJob.SingleData.with(itemId, apiServiceCall, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.ERROR) }
        verify(inverse = true) {  Sync.SingleData.with(TestIdRealmObject::class.java, idRealmObject, itemId).run() }
    }

    @Test
    fun shouldCallErrorAndNotCallSyncWhenJobFails() {
        every { mockApiService.apiServiceCall() } throws throwable
        runBlocking {
            RequestJob.SingleData.with(itemId, apiServiceCall, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.ERROR) }
        verify(inverse = true) {  Sync.SingleData.with(TestIdRealmObject::class.java, idRealmObject, itemId).run() }
    }

    @Test
    fun shouldCallTimeoutErrorAndNotCallSyncWhenJobFailsWithTimeout() {
        every { mockApiService.apiServiceCall() } throws timeoutThrowable
        runBlocking {
            RequestJob.SingleData.with(itemId, apiServiceCall, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.TIMEOUT) }
        verify(inverse = true) { Sync.SingleData.with(TestIdRealmObject::class.java, idRealmObject, itemId).run() }
    }
}
