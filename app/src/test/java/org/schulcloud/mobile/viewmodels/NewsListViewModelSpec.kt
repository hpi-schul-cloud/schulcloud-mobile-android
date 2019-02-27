package org.schulcloud.mobile.viewmodels

import io.mockk.*
import io.realm.Realm
import org.schulcloud.mobile.mockRealmDefaultInstance
import org.schulcloud.mobile.models.news.NewsRepository
import org.schulcloud.mobile.newsList
import org.schulcloud.mobile.prepareTaskExecutor
import org.schulcloud.mobile.resetTaskExecutor
import org.schulcloud.mobile.utils.asLiveData
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private val newsList = newsList(5)
private lateinit var mockRealm: Realm

object NewsListViewModelSpec : Spek({
    describe("A newsListViewModel") {
        val newsListViewModel by memoized {
            NewsListViewModel()
        }

        beforeEachTest {
            prepareTaskExecutor()
            mockRealm = mockk()
            mockRealmDefaultInstance(mockRealm)

            mockkObject(NewsRepository)
            every { NewsRepository.newsList(mockRealm) } returns newsList.asLiveData()
        }

        afterEach {
            resetTaskExecutor()
            unmockkAll()
        }

        describe("data access") {
            it("should return the correct data") {
                assertEquals(newsList, newsListViewModel.news.value)
            }
        }
    }
})
