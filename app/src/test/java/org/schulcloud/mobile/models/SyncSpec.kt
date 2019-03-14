package org.schulcloud.mobile.models

import io.mockk.*
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.RealmResults
import net.bytebuddy.matcher.ElementMatchers.ofType
import org.mockito.Mockito.*
import org.schulcloud.mobile.TestIdRealmObject
import org.schulcloud.mobile.prepareTaskExecutor
import org.schulcloud.mobile.resetTaskExecutor
import org.schulcloud.mobile.testIdRealmObjectList
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object SyncSpec : Spek({
    val idRealmObjects = testIdRealmObjectList(7)
    val items = idRealmObjects.take(4)
    val oldItems = idRealmObjects.takeLast(3)

    describe("") {
        val mockRealm = mock(Realm::class.java)
        val mockRealmResults = mockk<RealmResults<TestIdRealmObject>>()
        val mockRealmQuery = mockk<RealmQuery<TestIdRealmObject>>()
        mockkStatic(Realm::class)

        beforeEach {
            prepareTaskExecutor()
            // TODO: fix Realm usage
            every { Realm.getDefaultInstance() } returns mockRealm
            `when`(mockRealm.executeTransaction(any(Realm.Transaction::class.java))).thenCallRealMethod()
          //  doNothing().`when`(mockRealm.close())
            `when`(mockRealm.where(TestIdRealmObject::class.java)).thenReturn(mockRealmQuery)
            `when`(mockRealm.copyToRealmOrUpdate(any(TestIdRealmObject::class.java))).thenReturn(TestIdRealmObject())

            every { mockRealmQuery.findAll() } returns mockRealmResults
            every { mockRealmResults.iterator() } returns  idRealmObjects.iterator()
            every { mockRealmResults.size } returns idRealmObjects.size
        }

        afterEach {
            resetTaskExecutor()
            clearAllMocks()
        }

        describe("calling Sync without toDelete"){
            beforeEach {
                Sync.Data.with(TestIdRealmObject::class.java, items).run()
            }

            it("should add every new item to realm"){
                items.forEach{ item ->
                    verify(mockRealm).copyToRealmOrUpdate(item)
                }
            }

            it ("should delete every old item from realm"){
                oldItems.forEach { oldItem ->
                    verify { oldItem.deleteFromRealm() }
                }
            }
        }
    }
})

