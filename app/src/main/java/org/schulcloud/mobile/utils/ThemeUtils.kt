package org.schulcloud.mobile.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import org.schulcloud.mobile.storages.Preferences
import kotlin.properties.Delegates

object ThemeUtils {
    var themeConfig: ThemeConfig by Delegates.changeObservable(ThemeConfig(false)) { _, _, new ->
        themeChangeListeners.forEach { it(new) }
    }
        private set
    private val themeChangeListeners = mutableListOf<ThemeConfigChangeListener>()

    fun start(context: Context) {
        DarkModeUtils.start(context)
    }


    fun addThemeChangeListener(listener: ThemeConfigChangeListener) {
        themeChangeListeners.add(listener)
    }

    fun removeThemeChangeListener(listener: ThemeConfigChangeListener) {
        themeChangeListeners.remove(listener)
    }


    object DarkModeUtils {
        var isActive by Delegates.changeObservable(false) { _, _, new ->
            themeConfig = themeConfig.copy(darkMode = new)
        }
            private set

        private var ambientLightCriterion: AmbientLightCriterion? = null
        val supportsAmbientLight
            get() = ambientLightCriterion != null


        fun start(context: Context) {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .registerOnSharedPreferenceChangeListener { _, key ->
                        onPreferenceChange(key)
                    }

            if (AmbientLightCriterion.isSupported(context))
                ambientLightCriterion = AmbientLightCriterion(context).apply {
                    updateMonitoring()
                }

            updateDarkMode()
        }

        private fun onPreferenceChange(key: String) {
            when (key) {
                Preferences.Theme.DARK_MODE -> {
                    ambientLightCriterion?.updateMonitoring()
                    updateDarkMode()
                }
                Preferences.Theme.DARK_MODE_AMBIENT_LIGHT -> {
                    ambientLightCriterion?.updateMonitoring()
                }
            }
        }


        fun updateDarkMode() {
            val shouldBeActive = Preferences.Theme.darkMode
                    && (Preferences.Theme.darkMode_ambientLight implies (ambientLightCriterion?.shouldEnable ?: false))
            if (isActive == shouldBeActive) return

            AppCompatDelegate.setDefaultNightMode(if (shouldBeActive) MODE_NIGHT_YES else MODE_NIGHT_NO)
            isActive = shouldBeActive
        }


        abstract class Criterion {
            var shouldEnable by Delegates.changeObservable(false) { _, _, _ ->
                updateDarkMode()
            }
                protected set

            var isMonitoring = false
                private set

            fun updateMonitoring() {
                val shouldMonitor = Preferences.Theme.darkMode && shouldMonitor()
                if (isMonitoring == shouldMonitor) return

                isMonitoring = shouldMonitor
                if (shouldMonitor) startMonitoring()
                else stopMonitoring()
            }

            protected abstract fun shouldMonitor(): Boolean
            protected abstract fun startMonitoring()
            protected abstract fun stopMonitoring()
        }

        class AmbientLightCriterion(context: Context) : Criterion() {
            companion object {
                const val THRESHOLD = 10f

                fun isSupported(context: Context): Boolean {
                    return context.getSystemService<SensorManager>()
                            ?.getDefaultSensor(Sensor.TYPE_LIGHT) != null
                }
            }

            private var sensorManager = context.getSystemService<SensorManager>()!!
            private var sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)!!
            private val eventListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    val value = event?.values?.firstOrNull() ?: return
                    // TODO: Throttle
                    shouldEnable = value <= THRESHOLD
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }


            override fun shouldMonitor() = Preferences.Theme.darkMode_ambientLight

            override fun startMonitoring() {
                sensorManager.registerListener(eventListener, sensor, SensorManager.SENSOR_DELAY_UI)
            }

            override fun stopMonitoring() {
                sensorManager.unregisterListener(eventListener)
            }
        }
    }
}

data class ThemeConfig(val darkMode: Boolean)

typealias ThemeConfigChangeListener = (ThemeConfig) -> Unit
