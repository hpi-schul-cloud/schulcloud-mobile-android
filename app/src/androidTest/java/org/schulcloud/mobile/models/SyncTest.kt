package org.schulcloud.mobile.models

import io.realm.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.schulcloud.mobile.commonTest.TestIdRealmObject
import org.schulcloud.mobile.config.Config
import org.schulcloud.mobile.commonTest.testIdRealmObjectList
import org.schulcloud.mobile.utils.it


class SyncTest {
    private val idRealmObjects = testIdRealmObjectList(7)
    private val items = idRealmObjects.take(4)
    private val moreItems = idRealmObjects.take(6)
    private val testConfig = RealmConfiguration.Builder().name("test-realm").schemaVersion(Config.REALM_SCHEMA_VERSION).build()
    private val deleteQuery: (RealmQuery<TestIdRealmObject>.() -> RealmQuery<TestIdRealmObject>) = ::it
    private val idDeleteQuery: (RealmQuery<TestIdRealmObject>.() -> RealmQuery<TestIdRealmObject>) = {equalTo("id", "7")}
    private lateinit var testRealm: Realm


    @Before
    fun setUp() {
        Realm.setDefaultConfiguration(testConfig)
        testRealm = Realm.getDefaultInstance()
    }

    @After
    fun tearDown() {
        testRealm.executeTransaction {
            testRealm.deleteAll()
        }
        testRealm.close()
    }

    @Test
    fun shouldAddEveryNewItemToRealm() {
        Sync.Data.with(TestIdRealmObject::class.java, items, deleteQuery).run()

        val realmObjects = testRealm.where(TestIdRealmObject::class.java).sort("id", Sort.ASCENDING).findAll()
        assertEquals(items.size, realmObjects.size)
        items.forEachIndexed { index, item ->
            assertEquals(item.id, realmObjects[index]?.id)
        }
    }

    @Test
    fun shouldDeleteEveryOldItemFromRealm() {
        addIdRealmObjectsToTestRealm()

        Sync.Data.with(TestIdRealmObject::class.java, items, deleteQuery).run()

        val realmObjects = testRealm.where(TestIdRealmObject::class.java).sort("id", Sort.ASCENDING).findAll()
        assertEquals(items.size, realmObjects.size)
        items.forEachIndexed { index, item ->
            assertEquals(item.id, realmObjects[index]?.id)
        }
    }

    @Test
    fun shouldOnlyDeleteItemsMatchingDeleteQuery() {
        addIdRealmObjectsToTestRealm()

        Sync.Data.with(TestIdRealmObject::class.java, items, idDeleteQuery).run()

        val realmObjects = testRealm.where(TestIdRealmObject::class.java).sort("id", Sort.ASCENDING).findAll()
        assertEquals(moreItems.size, realmObjects.size)
        moreItems.forEachIndexed { index, item ->
            assertEquals(item.id, realmObjects[index]?.id)
        }
    }

    @Test
    fun shouldNotDeleteAnyItemsWithoutDeleteQuery() {
        addIdRealmObjectsToTestRealm()

        Sync.Data.with(TestIdRealmObject::class.java, items).run()

        val realmObjects = testRealm.where(TestIdRealmObject::class.java).sort("id", Sort.ASCENDING).findAll()
        assertEquals(idRealmObjects.size, realmObjects.size)
        idRealmObjects.forEachIndexed { index, item ->
            assertEquals(item.id, realmObjects[index]?.id)
        }
    }

    private fun addIdRealmObjectsToTestRealm(){
        testRealm.executeTransaction {

            for (item in idRealmObjects)
                testRealm.copyToRealmOrUpdate(item)

            testRealm.copyToRealmOrUpdate(idRealmObjects)
        }
    }
}
