package org.schulcloud.mobile.models.base

import androidx.lifecycle.Observer
import io.mockk.*
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmModel
import io.realm.RealmResults
import org.schulcloud.mobile.prepareTaskExecutor
import org.schulcloud.mobile.realmModelList
import org.schulcloud.mobile.resetTaskExecutor
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object LiveRealmDataSpec : Spek({
    val mockRealmResults = mockk<RealmResults<RealmModel>>()


    describe("A liveRealmData") {
        val liveRealmData by memoized {
            LiveRealmData(mockRealmResults)
        }
        val mockRealm = mockk<Realm>()
        val realmModels = realmModelList(5)
        val observer = spyk<Observer<List<RealmModel>>>()
        val listenerSlot = slot<RealmChangeListener<RealmResults<RealmModel>>>()

        mockkStatic(Realm::class)

        beforeEach {
            prepareTaskExecutor()
            every { Realm.getDefaultInstance() } returns mockRealm
            every { mockRealm.copyFromRealm(mockRealmResults) } returns realmModels
            every { mockRealmResults.addChangeListener(ofType<RealmChangeListener<RealmResults<RealmModel>>>()) } just runs
            every { mockRealmResults.isLoaded } returns true
        }

        afterEach {
            resetTaskExecutor()
            clearAllMocks()
        }


        describe("observing liveRealmData") {
            beforeEach {
                liveRealmData.observeForever(observer)
            }
            it("should listen to realm changes") {
                verify { mockRealmResults.addChangeListener(ofType<RealmChangeListener<RealmResults<RealmModel>>>()) }
            }
        }

        describe("changing realm results") {
            beforeEach {
                liveRealmData.observeForever(observer)

            }
            it("should update its value") {
                verify { mockRealmResults.addChangeListener(capture(listenerSlot)) }
                every { mockRealmResults.load() } answers {
                    listenerSlot.captured.onChange(mockRealmResults)
                    true
                }
                mockRealmResults.load()
                verify { observer.onChanged(realmModels) }
            }
        }
    }
})
