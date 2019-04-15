package org.schulcloud.mobile.viewmodels

import io.mockk.*
import io.realm.Realm
import org.schulcloud.mobile.commonTest.homeworkList
import org.schulcloud.mobile.commonTest.mockRealmDefaultInstance
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.commonTest.prepareTaskExecutor
import org.schulcloud.mobile.commonTest.resetTaskExecutor
import org.schulcloud.mobile.utils.asLiveData
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object HomeworkWidgetViewModelSpec : Spek({
    val homeworkList = homeworkList(5)

    describe("A homeworkWidgetViewModel") {
        val homeworkWidgetViewModel by memoized {
            HomeworkWidgetViewModel()
        }
        val mockRealm = mockk<Realm>()
        mockRealmDefaultInstance(mockRealm)
        mockkObject(HomeworkRepository)

        beforeEach {
            prepareTaskExecutor()
            every { HomeworkRepository.openHomeworkForNextWeek(mockRealm) } returns homeworkList.asLiveData()
        }

        afterEach {
            resetTaskExecutor()
            clearAllMocks()
        }

        describe("data access") {
            it("should return the correct data") {
                assertEquals(homeworkList, homeworkWidgetViewModel.homework.value)
            }
        }
    }
})