package org.schulcloud.mobile.models.base

import androidx.lifecycle.Observer
import io.mockk.*
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmObject
import org.schulcloud.mobile.commonTest.prepareTaskExecutor
import org.schulcloud.mobile.commonTest.resetTaskExecutor
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object RealmObjectLiveDataSpec : Spek({
    val mockRealmObject = mockk<RealmObject>(relaxUnitFun = true)

    describe("A realmObjectLiveData") {
        val realmObjectLiveData by memoized {
            RealmObjectLiveData(mockRealmObject)
        }
        val mockRealm = mockk<Realm>()
        val realmObject = object : RealmObject() {}
        val observer = spyk<Observer<RealmObject?>>()
        val listenerSlot = slot<RealmChangeListener<RealmObject>>()

        mockkStatic(Realm::class)

        beforeEach {
            prepareTaskExecutor()
            every { Realm.getDefaultInstance() } returns mockRealm
            every { mockRealm.copyFromRealm(mockRealmObject) } returns realmObject
            every { mockRealmObject.isLoaded } returns true
        }

        afterEach {
            resetTaskExecutor()
            clearAllMocks()
        }

        describe("observing realmObjectLiveData") {
            beforeEach {
                realmObjectLiveData.observeForever(observer)
            }
            it("should listen to realm changes") {
                verify { mockRealmObject.addChangeListener(ofType<RealmChangeListener<RealmObject>>()) }
            }
        }

        describe("changing realmObject") {
            beforeEach {
                realmObjectLiveData.observeForever(observer)

            }
            it("should update its value") {
                verify { mockRealmObject.addChangeListener(capture(listenerSlot)) }
                every { mockRealmObject.load() } answers {
                    listenerSlot.captured.onChange(mockRealmObject)
                    true
                }
                mockRealmObject.load()
                verify { observer.onChanged(realmObject) }
            }
        }
    }
})
