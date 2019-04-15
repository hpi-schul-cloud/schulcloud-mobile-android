package org.schulcloud.mobile.viewmodels

import io.mockk.*
import io.realm.Realm
import org.schulcloud.mobile.commonTest.*
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.utils.asLiveData
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File
import kotlin.test.assertEquals

object FileViewModelSpec : Spek({
    val fileList = fileList(5)
    val directoryList = directoryList(5)
    val path = "path"
    val fixedPath = "path" + File.separator

    describe("A fileViewModel") {
        val fileViewModel by memoized {
            FileViewModel(path)
        }
        val mockRealm = mockk<Realm>()
        mockRealmDefaultInstance(mockRealm)
        mockkObject(FileRepository)

        beforeEach {
            prepareTaskExecutor()
            every { FileRepository.fixPath(path) } returns fixedPath
            every { FileRepository.files(mockRealm, fixedPath) } returns fileList.asLiveData()
            every { FileRepository.directories(mockRealm, fixedPath) } returns directoryList.asLiveData()
        }

        afterEach {
            resetTaskExecutor()
            clearAllMocks()
        }

        describe("data access") {
            it("should return the correct data") {
                assertEquals(fixedPath, fileViewModel.path)
                assertEquals(fileList, fileViewModel.files.value)
                assertEquals(directoryList, fileViewModel.directories.value)
            }
        }
    }
})

