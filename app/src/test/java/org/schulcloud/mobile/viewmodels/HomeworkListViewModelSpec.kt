package org.schulcloud.mobile.viewmodels

import io.mockk.*
import io.realm.Realm
import org.schulcloud.mobile.homeworkList
import org.schulcloud.mobile.mockRealmDefaultInstance
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.prepareTaskExecutor
import org.schulcloud.mobile.resetTaskExecutor
import org.schulcloud.mobile.utils.asLiveData
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private val homeworkList = homeworkList(5)
private lateinit var mockRealm: Realm

object HomeworkListViewModelSpec : Spek({
    describe("A homeworkListViewModel") {
        val homeworkListViewModel by memoized {
            HomeworkListViewModel()
        }

        beforeEachTest {
            prepareTaskExecutor()
            mockRealm = mockk()
            mockRealmDefaultInstance(mockRealm)

            mockkObject(HomeworkRepository)
            every { HomeworkRepository.homeworkList(mockRealm) } returns homeworkList.asLiveData()
        }

        afterEach {
            resetTaskExecutor()
            unmockkAll()
        }

        describe("data access") {
            it("should return the correct data") {
                assertEquals(homeworkList, homeworkListViewModel.homework.value)
            }
        }
    }
})
