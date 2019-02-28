package org.schulcloud.mobile.viewmodels

import io.mockk.*
import io.realm.Realm
import org.schulcloud.mobile.models.news.NewsRepository
import org.schulcloud.mobile.newsList
import org.schulcloud.mobile.prepareTaskExecutor
import org.schulcloud.mobile.resetTaskExecutor
import org.schulcloud.mobile.utils.asLiveData
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals


object NewsListViewModelSpec : Spek({
    val newsList = newsList(5)

    describe("A newsListViewModel") {
        val newsListViewModel by memoized {
            NewsListViewModel()
        }
        val mockRealm = mockk<Realm>()
        mockkObject(NewsRepository)
        mockkStatic(Realm::class)

        beforeEachTest {
            prepareTaskExecutor()
            every { NewsRepository.newsList(mockRealm) } returns newsList.asLiveData()
            every { Realm.getDefaultInstance() } returns mockRealm
        }

        afterEach {
            resetTaskExecutor()
            clearAllMocks()
        }

        describe("data access") {
            it("should return the correct data") {
                assertEquals(newsList, newsListViewModel.news.value)
            }
        }
    }
})
