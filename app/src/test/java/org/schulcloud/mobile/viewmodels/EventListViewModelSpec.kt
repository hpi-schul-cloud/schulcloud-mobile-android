package org.schulcloud.mobile.viewmodels

import io.mockk.*
import io.realm.Realm
import org.schulcloud.mobile.eventList
import org.schulcloud.mobile.models.event.EventRepository
import org.schulcloud.mobile.prepareTaskExecutor
import org.schulcloud.mobile.resetTaskExecutor
import org.schulcloud.mobile.utils.asLiveData
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object EventListViewModelSpec : Spek({
    val events = eventList(5)

    describe("A eventListViewModel") {
        val eventListViewModel by memoized {
            EventListViewModel()
        }
        val mockRealm = mockk<Realm>()
        mockkObject(EventRepository)
        mockkStatic(Realm::class)

        beforeEachTest {
            prepareTaskExecutor()
            every { EventRepository.eventsForToday(mockRealm) } returns events.asLiveData()
            every { Realm.getDefaultInstance() } returns mockRealm
        }

        afterEach {
            resetTaskExecutor()
            clearAllMocks()
        }

        describe("data access") {
            it("should return the correct data") {
                assertEquals(events, eventListViewModel.events.value)
            }
        }
    }
})
