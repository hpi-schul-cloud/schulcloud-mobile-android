package org.schulcloud.mobile.storages

import android.content.Context
import android.os.Build
import androidx.core.content.edit
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockkObject
import org.junit.After
import org.junit.Before
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.schulcloud.mobile.SchulCloudApp
import org.schulcloud.mobile.SchulCloudTestApp

@RunWith(RobolectricTestRunner::class)
@Config(application = SchulCloudTestApp::class)
class PreferencesTest {
    private val packageName = "org.schulcloud.mobile"
    private val preferencesName = packageName + "_preferences"
    private val defaultDarkMode = false
    private val darkMode = true
    private val defaultDarkModeAmbientLight = false
    private val darkModeAmbientLight = true
    private val defaultDarkModeNight = false
    private val darkModeNight = true
    private val defaultDarkModePowerSaver = false
    private val darkModePowerSaver = true
    private val defaultCrashlytics = true
    private val crashlytics = false
    private val defaultVersion = true
    private val version = false
    private val sharedPreferences = ApplicationProvider.getApplicationContext<SchulCloudTestApp>()
            .getSharedPreferences("test_preferences", Context.MODE_PRIVATE)

    @Before
    fun setUp() {
        mockkObject(SchulCloudApp)
        every { SchulCloudApp.instance.getSharedPreferences(preferencesName, Context.MODE_PRIVATE) } returns sharedPreferences
        every { SchulCloudApp.instance.packageName } returns packageName
    }

    @After
    fun tearDown() {
        Preferences.clear()
        Preferences.Theme.clear()
        Preferences.Privacy.clear()
        Preferences.About.clear()
    }

    @Test
    fun shouldStoreDarkMode() {
        Preferences.Theme.darkMode = darkMode
        assertEquals(darkMode, Preferences.Theme.darkMode)
    }

    @Test
    fun shouldProvideCorrectDefaultDarkMode() {
        assertEquals(defaultDarkMode, Preferences.Theme.darkMode)
    }

    @Test
    fun shouldStoreDarkModeAmbientLight() {
        Preferences.Theme.darkMode_ambientLight = darkModeAmbientLight
        assertEquals(darkModeAmbientLight, Preferences.Theme.darkMode_ambientLight)
    }

    @Test
    fun shouldProvideCorrectDefaultDarkModeAmbientLight() {
        assertEquals(defaultDarkModeAmbientLight, Preferences.Theme.darkMode_ambientLight)
    }

    @Test
    fun shouldStoreDarkModeNight() {
        Preferences.Theme.darkMode_night = darkModeNight
        assertEquals(darkModeNight, Preferences.Theme.darkMode_night)
    }

    @Test
    fun shouldProvideCorrectDefaultDarkModeNight() {
        assertEquals(defaultDarkModeNight, Preferences.Theme.darkMode_night)
    }

    @Test
    fun shouldStoreDarkModePowerSaver() {
        Preferences.Theme.darkMode_powerSaver = darkModePowerSaver
        assertEquals(darkModePowerSaver, Preferences.Theme.darkMode_powerSaver)
    }

    @Test
    fun shouldProvideCorrectDefaultDarkModePowerSaver() {
        assertEquals(defaultDarkModePowerSaver, Preferences.Theme.darkMode_powerSaver)
    }

    @Test
    fun shouldStoreCrashlytics() {
        Preferences.Privacy.crashlytics = crashlytics
        assertEquals(crashlytics, Preferences.Privacy.crashlytics)
    }

    @Test
    fun shouldProvideCorrectDefaultCrashlytics() {
        assertEquals(defaultCrashlytics, Preferences.Privacy.crashlytics)
    }

    @Test
    fun shouldStoreVersion() {
        Preferences.About.version = version
        assertEquals(version, Preferences.About.version)
    }

    @Test
    fun shouldProvideCorrectDefaultVersion() {
        assertEquals(defaultVersion, Preferences.About.version)
    }

    @Test
    fun shouldDeleteAllStoredValuesWhenCleared() {
        Preferences.Theme.darkMode = darkMode
        Preferences.Theme.darkMode_ambientLight = darkModeAmbientLight
        Preferences.Theme.darkMode_night = darkModeNight
        Preferences.Theme.darkMode_powerSaver = darkModePowerSaver
        Preferences.Privacy.crashlytics = crashlytics
        Preferences.About.version = version

        Preferences.clear()
        Preferences.Theme.clear()
        Preferences.Privacy.clear()
        Preferences.About.clear()

        assertEquals(defaultDarkMode, Preferences.Theme.darkMode)
        assertEquals(defaultDarkModeAmbientLight, Preferences.Theme.darkMode_ambientLight)
        assertEquals(defaultDarkModeNight, Preferences.Theme.darkMode_night)
        assertEquals(defaultDarkModePowerSaver, Preferences.Theme.darkMode_powerSaver)
        assertEquals(defaultCrashlytics, Preferences.Privacy.crashlytics)
        assertEquals(defaultVersion, Preferences.About.version)
    }
}
