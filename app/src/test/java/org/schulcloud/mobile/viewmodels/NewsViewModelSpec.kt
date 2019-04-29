package org.schulcloud.mobile.viewmodels

import io.mockk.*
import io.realm.Realm
import org.schulcloud.mobile.commonTest.mockRealmDefaultInstance
import org.schulcloud.mobile.models.news.NewsRepository
import org.schulcloud.mobile.commonTest.news
import org.schulcloud.mobile.commonTest.prepareTaskExecutor
import org.schulcloud.mobile.commonTest.resetTaskExecutor
import org.schulcloud.mobile.utils.asLiveData
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object NewsViewModelSpec : Spek({
    val id = "id"
    val news = news(id)

    describe("A newsViewModel") {
        val newsViewModel by memoized {
            NewsViewModel(id)
        }
        val mockRealm = mockk<Realm>()

        beforeGroup {
            mockRealmDefaultInstance(mockRealm)
            mockkObject(NewsRepository)
        }

        beforeEach {
            prepareTaskExecutor()
            every { NewsRepository.news(mockRealm, id) } returns news.asLiveData()
        }

        afterEach {
            resetTaskExecutor()
            clearAllMocks()
        }

        describe("data access") {
            it("should return the correct data") {
                assertEquals(news, newsViewModel.news.value)
            }
        }
    }
})
