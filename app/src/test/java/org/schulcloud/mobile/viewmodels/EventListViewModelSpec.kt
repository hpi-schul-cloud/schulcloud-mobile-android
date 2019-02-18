package org.schulcloud.mobile.viewmodels

import io.mockk.*
import io.realm.Realm
import org.schulcloud.mobile.eventList
import org.schulcloud.mobile.mockRealmDefaultInstance
import org.schulcloud.mobile.models.event.EventRepository
import org.schulcloud.mobile.prepareTaskExecutor
import org.schulcloud.mobile.resetTaskExecutor
import org.schulcloud.mobile.utils.asLiveData
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private val events = eventList(5)
private lateinit var mockRealm: Realm

object EventListViewModelSpec : Spek({
    describe("A eventListViewModel") {
        val eventListViewModel by memoized {
            EventListViewModel()
        }

        beforeEachTest {
            prepareTaskExecutor()
            mockRealm = mockk()
            mockRealmDefaultInstance(mockRealm)

            mockkObject(EventRepository)
            every { EventRepository.eventsForToday(mockRealm) } returns events.asLiveData()
        }

        afterEach {
            resetTaskExecutor()
            unmockkObject(EventRepository)
            unmockkStatic(Realm::class)
        }

        describe("data access") {
            it("should return the correct data") {
                assertEquals(events, eventListViewModel.events.value)
            }
        }
    }
})
