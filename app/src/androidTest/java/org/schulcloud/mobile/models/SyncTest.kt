package org.schulcloud.mobile.models

import io.realm.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.schulcloud.mobile.commonTest.TestIdRealmObject
import org.schulcloud.mobile.commonTest.testIdRealmObject
import org.schulcloud.mobile.config.Config
import org.schulcloud.mobile.commonTest.testIdRealmObjectList
import org.schulcloud.mobile.utils.it


class SyncTest {
    private val idRealmObjects = testIdRealmObjectList(7)
    private val singleItem = testIdRealmObject(2.toString())
    private val toDeleteItemId = 7.toString()
    private val items = idRealmObjects.take(4)
    private val moreItems = idRealmObjects.take(6)
    private val testConfig = RealmConfiguration.Builder().name("test-realm").schemaVersion(Config.REALM_SCHEMA_VERSION).build()
    private val deleteQuery: (RealmQuery<TestIdRealmObject>.() -> RealmQuery<TestIdRealmObject>) = ::it
    private val idDeleteQuery: (RealmQuery<TestIdRealmObject>.() -> RealmQuery<TestIdRealmObject>) = {equalTo("id", toDeleteItemId)}
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
    fun shouldAddEveryNewDataItemToRealm() {
        Sync.Data.with(TestIdRealmObject::class.java, items, deleteQuery).run()

        val realmObjects = getOrderedTestIdRealmObjects(testRealm)
        assertItemListEquality(items, realmObjects)
    }

    @Test
    fun shouldDeleteEveryOldItemFromRealm() {
        addIdRealmObjectsToTestRealm()

        Sync.Data.with(TestIdRealmObject::class.java, items, deleteQuery).run()

        val realmObjects = getOrderedTestIdRealmObjects(testRealm)
        assertItemListEquality(items, realmObjects)
    }

    @Test
    fun shouldOnlyDeleteItemsMatchingDeleteQuery() {
        addIdRealmObjectsToTestRealm()

        Sync.Data.with(TestIdRealmObject::class.java, items, idDeleteQuery).run()

        val realmObjects = getOrderedTestIdRealmObjects(testRealm)
        assertItemListEquality(moreItems, realmObjects)
    }

    @Test
    fun shouldNotDeleteAnyItemsWithoutDeleteQuery() {
        addIdRealmObjectsToTestRealm()

        Sync.Data.with(TestIdRealmObject::class.java, items).run()

        val realmObjects = getOrderedTestIdRealmObjects(testRealm)
        assertItemListEquality(idRealmObjects, realmObjects)
    }

    @Test
    fun shouldAddNewSingleDataItemToRealm() {
        Sync.SingleData.with(TestIdRealmObject::class.java, singleItem).run()

        val realmObjects = testRealm.where(TestIdRealmObject::class.java).findAll()
        assertEquals(1, realmObjects.size)
        assertEquals(singleItem.id, realmObjects.first()?.id)
    }

    @Test
    fun shouldDeleteItemWithGivenDeleteIdFromRealm(){
        addIdRealmObjectsToTestRealm()

        Sync.SingleData.with(TestIdRealmObject::class.java, singleItem, toDeleteItemId).run()

        val realmObjects = getOrderedTestIdRealmObjects(testRealm)
        assertItemListEquality(moreItems, realmObjects)
    }

    @Test
    fun shouldNotDeleteAnyItemsWithoutDeleteId() {
        addIdRealmObjectsToTestRealm()

        Sync.SingleData.with(TestIdRealmObject::class.java, singleItem).run()

        val realmObjects = getOrderedTestIdRealmObjects(testRealm)
        assertItemListEquality(idRealmObjects, realmObjects)
    }

    @Test
    fun shouldNotDeleteGivenNewItem(){
        addIdRealmObjectsToTestRealm()

        Sync.SingleData.with(TestIdRealmObject::class.java, singleItem, singleItem.id).run()

        val realmObjects = getOrderedTestIdRealmObjects(testRealm)
        assertItemListEquality(idRealmObjects, realmObjects)
    }

    private fun addIdRealmObjectsToTestRealm(){
        testRealm.executeTransaction {

            for (item in idRealmObjects)
                testRealm.copyToRealmOrUpdate(item)

            testRealm.copyToRealmOrUpdate(idRealmObjects)
        }
    }

    private fun getOrderedTestIdRealmObjects(realm: Realm): List<TestIdRealmObject?>
            = realm.where(TestIdRealmObject::class.java).sort("id", Sort.ASCENDING).findAll()

    private fun assertItemListEquality(expected: List<TestIdRealmObject>, actual: List<TestIdRealmObject?>){
        assertEquals(expected.size, actual.size)
        expected.forEachIndexed { index, item ->
            assertEquals(item.id, actual[index]?.id)
        }
    }
}
