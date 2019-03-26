package org.schulcloud.mobile.storages

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockkObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.schulcloud.mobile.SchulCloudApp
import org.schulcloud.mobile.SchulCloudTestApp

@RunWith(RobolectricTestRunner::class)
@Config(application = SchulCloudTestApp::class)
class OnboardingTest {
    private val onboardingName = "pref_onboarding"
    private val versionNavigation = Onboarding.NAVIGATION_1
    private val lowerVersionNavigation = 0
    private val versionNavigationRange = 1..Onboarding.NAVIGATION_1
    private val sharedPreferences = ApplicationProvider.getApplicationContext<SchulCloudTestApp>()
            .getSharedPreferences("test_preferences", Context.MODE_PRIVATE)

    @Before
    fun setUp() {
        mockkObject(SchulCloudApp)
        every { SchulCloudApp.instance.getSharedPreferences(onboardingName, Context.MODE_PRIVATE) } returns sharedPreferences
    }

    @After
    fun tearDown() {
        Onboarding.clear()
    }

    @Test
    fun shouldGetNavigationVersionUpdateWhenNotStoredBefore(){
        assertEquals(versionNavigationRange, Onboarding.navigation.getUpdates())
    }

    @Test
    fun shouldNotGetNavigationVersionUpdateWhenAlreadyStoredBefore(){
        Onboarding.navigation.update(versionNavigation)
        assertEquals(null, Onboarding.navigation.getUpdates())
    }

    @Test
    fun shouldUpdateNavigationVersionWhenNotStoredBeforeAndUpdatesTrue(){
        assertEquals(versionNavigationRange, Onboarding.navigation.getUpdates(true))
        assertEquals(null, Onboarding.navigation.getUpdates())
    }


    @Test
    fun shouldBeAbleToDowngradeNavigationVersion(){
        Onboarding.navigation.update(lowerVersionNavigation)
        assertEquals(versionNavigationRange, Onboarding.navigation.getUpdates())
    }

    @Test
    fun shouldDeleteAllStoredVersionsWhenCleared() {
        Onboarding.navigation.update(versionNavigation)

        Onboarding.clear()

        assertEquals(versionNavigationRange, Onboarding.navigation.getUpdates())
    }
}
