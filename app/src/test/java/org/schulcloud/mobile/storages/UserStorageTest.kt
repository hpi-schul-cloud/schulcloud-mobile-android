package org.schulcloud.mobile.storages

import android.content.Context
import androidx.core.content.edit
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockkObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.schulcloud.mobile.SchulCloudApp
import org.schulcloud.mobile.SchulCloudTestApp
import org.schulcloud.mobile.commonTest.rules.SharedPreferencesRule
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
@Config(application = SchulCloudTestApp::class)
class UserStorageTest {
    private val userStorageName = "pref_user_v2"
    private val userId = "userId"
    private val accessToken = "accessToken"
    private val sharedPreferences = ApplicationProvider.getApplicationContext<SchulCloudTestApp>()
            .getSharedPreferences("test_preferences", Context.MODE_PRIVATE)

    @Rule
    @JvmField
    val sharedPreferencesRule = SharedPreferencesRule(userStorageName, sharedPreferences)

    @After
    fun tearDown() {
        UserStorage.clear()
    }

    @Test
    fun shouldStoreUserId() {
        UserStorage.userId = userId
        assertEquals(userId, UserStorage.userId)
    }

    @Test
    fun shouldProvideDefaultUserIdNull() {
        assertNull(UserStorage.userId)
    }

    @Test
    fun shouldStoreAccessToken() {
        UserStorage.accessToken = accessToken
        assertEquals(accessToken, UserStorage.accessToken)
    }

    @Test
    fun shouldProvideDefaultAccessTokenNull() {
        assertNull(UserStorage.accessToken)
    }

    @Test
    fun shouldDeleteAllStoredValuesWhenCleared() {
        UserStorage.userId = userId
        UserStorage.accessToken = accessToken

        UserStorage.clear()

        assertNull(UserStorage.userId)
        assertNull(UserStorage.accessToken)
    }
}
