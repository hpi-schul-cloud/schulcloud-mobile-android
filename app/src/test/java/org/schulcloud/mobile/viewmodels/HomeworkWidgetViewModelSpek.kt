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

object HomeworkWidgetViewModelSpec : Spek({
    describe("A homeworkWidgetViewModel") {
        val homeworkWidgetViewModel by memoized {
            HomeworkWidgetViewModel()
        }

        beforeEachTest {
            prepareTaskExecutor()
            mockRealm = mockk()
            mockRealmDefaultInstance(mockRealm)

            mockkObject(HomeworkRepository)
            every { HomeworkRepository.openHomeworkForNextWeek(mockRealm) } returns homeworkList.asLiveData()
        }

        afterEach {
            resetTaskExecutor()
            unmockkObject(HomeworkRepository)
            unmockkStatic(Realm::class)
        }

        describe("data access") {
            it("should return the correct data") {
                assertEquals(homeworkList, homeworkWidgetViewModel.homework.value)
            }
        }
    }
})
