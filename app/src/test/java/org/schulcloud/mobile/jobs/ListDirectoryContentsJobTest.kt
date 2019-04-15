package org.schulcloud.mobile.jobs

import io.mockk.*
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.schulcloud.mobile.commonTest.directoryList
import org.schulcloud.mobile.commonTest.fileList
import org.schulcloud.mobile.commonTest.rules.CoroutinesRule
import org.schulcloud.mobile.commonTest.rules.RequestJobTestRule
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.file.Directory
import org.schulcloud.mobile.models.file.DirectoryResponse
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.network.ApiServiceInterface
import org.schulcloud.mobile.utils.NetworkUtil
import retrofit2.Response
import ru.gildor.coroutines.retrofit.awaitResponse

class ListDirectoryContentsJobTest {
    private val mockApiService = mockk<ApiServiceInterface>()
    private val path = "path"
    private val files = fileList(3)
    private val directories = directoryList(3)
    private val callback = spyk<RequestJobCallback>()
    private val directoryResponse = DirectoryResponse().also {
        it.files = files
        it.directories = directories
    }
    private val mockResponse = mockk<Response<DirectoryResponse>>()
    private val throwable = Throwable()
    private val timeoutThrowable = Throwable("connect timed out")
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Rule
    @JvmField
    val coroutinesRule = CoroutinesRule(mainThreadSurrogate)

    @Rule
    @JvmField
    val requestJobTestRule = RequestJobTestRule(mockApiService, directoryResponse, mockResponse)

    @Before
    fun setUp() {
        mockkObject(Sync.Data)
        every { Sync.Data.with(File::class.java, files).run() } just runs
        every { Sync.Data.with(Directory::class.java, directories).run() } just runs
        every { mockApiService.listDirectoryContents(path) } returns mockk {
            coEvery { awaitResponse() } returns mockResponse
        }
    }

    @Test
    fun shouldCallApiServiceWhenOnline() {
        runBlocking {
            ListDirectoryContentsJob(path, callback).run()
        }
        verify { mockApiService.listDirectoryContents(path) }
    }

    @Test
    fun shouldCallNoNetworkErrorWhenNotOnline() {
        every { NetworkUtil.isOnline() } returns false
        runBlocking {
            ListDirectoryContentsJob(path, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.NO_NETWORK) }
        verify(inverse = true) { mockApiService.listDirectoryContents(path) }
    }

    @Test
    fun shouldCallSyncWhenApiServiceCallSuccessful() {
        runBlocking {
            ListDirectoryContentsJob(path, callback).run()
        }
        verify {
            Sync.Data.with(File::class.java, files).run()
            Sync.Data.with(Directory::class.java, directories).run()
        }
    }

    @Test
    fun shouldCallErrorAndNotCallSyncWhenApiServiceCallNotSuccessful() {
        every { mockResponse.isSuccessful } returns false
        runBlocking {
            ListDirectoryContentsJob(path, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.ERROR) }
        verify(inverse = true) {
            Sync.Data.with(File::class.java, files).run()
            Sync.Data.with(Directory::class.java, directories).run()
        }
    }

    @Test
    fun shouldCallErrorAndNotCallSyncWhenJobFails() {
        every { mockApiService.listDirectoryContents(path) } throws throwable
        runBlocking {
            ListDirectoryContentsJob(path, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.ERROR) }
        verify(inverse = true) {
            Sync.Data.with(File::class.java, files).run()
            Sync.Data.with(Directory::class.java, directories).run()
        }
    }

    @Test
    fun shouldCallTimeoutErrorAndNotCallSyncWhenJobFailsWithTimeout() {
        every { mockApiService.listDirectoryContents(path) } throws timeoutThrowable
        runBlocking {
            ListDirectoryContentsJob(path, callback).run()
        }
        verify { callback.error(RequestJobCallback.ErrorCode.TIMEOUT) }
        verify(inverse = true) {
            Sync.Data.with(File::class.java, files).run()
            Sync.Data.with(Directory::class.java, directories).run()
        }
    }
}
