package org.schulcloud.mobile.storages.base

import android.content.Context
import androidx.core.content.edit
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockkObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.schulcloud.mobile.SchulCloudApp
import org.schulcloud.mobile.SchulCloudTestApp

@RunWith(RobolectricTestRunner::class)
@Config(application = SchulCloudTestApp::class)
class BaseStorageTest {
    private val name = "name"
    private val booleanKey = "booleanKey"
    private val booleanValue = true
    private val intKey = "intKey"
    private val intValue = 1
    private val stringKey = "stringKey"
    private val stringValue = "stringValue"
    private val sharedPreferences = ApplicationProvider.getApplicationContext<SchulCloudTestApp>()
            .getSharedPreferences("test_preferences", Context.MODE_PRIVATE)
    private lateinit var storage: BaseStorage

    @Before
    fun setUp() {
        mockkObject(SchulCloudApp)
        every { SchulCloudApp.instance.getSharedPreferences(name, Context.MODE_PRIVATE) } returns sharedPreferences
        storage = object : BaseStorage(name){}
    }

    @After
    fun tearDown(){
        sharedPreferences.edit { clear() }
    }

    @Test
    fun shouldStoreBoolean(){
        storage.putBoolean(booleanKey, booleanValue)
        assertEquals(booleanValue, storage.getBoolean(booleanKey))
    }

    @Test
    fun shouldStoreInt() {
        storage.putInt(intKey, intValue)
        assertEquals(intValue, storage.getInt(intKey))
    }

    @Test
    fun shouldStoreString(){
        storage.putString(stringKey, stringValue)
        assertEquals(stringValue, storage.getString(stringKey))
    }
}
