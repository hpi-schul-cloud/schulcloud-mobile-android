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
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.properties.Delegates

class ThemeConfigUtils private constructor(val context: Context) {
    companion object : SingletonHolder<ThemeConfigUtils, Context>({ ThemeConfigUtils(it.applicationContext) })

    var themeConfig: ThemeConfig by Delegates.changeObservable(ThemeConfig(false)) { _, _, new ->
        themeChangeListeners.forEach { it(new) }
    }
        private set
    private val themeChangeListeners = mutableListOf<ThemeConfigChangeListener>()

    init {
        DarkModeUtils.getInstance(context)
    }


    fun addThemeChangeListener(listener: ThemeConfigChangeListener) {
        themeChangeListeners.add(listener)
    }

    fun removeThemeChangeListener(listener: ThemeConfigChangeListener) {
        themeChangeListeners.remove(listener)
    }

    fun notifyThemeConfigChanged() {
        val new = ThemeConfig(DarkModeUtils.getInstance(context).isActive)
        if (themeConfig == new) return

        themeConfig = new
    }
}

data class ThemeConfig(val darkMode: Boolean)

typealias ThemeConfigChangeListener = (ThemeConfig) -> Unit


class DarkModeUtils(val context: Context) {
    companion object : SingletonHolder<DarkModeUtils, Context>({ DarkModeUtils(it.applicationContext) })

    var isActive by Delegates.changeObservable(false) { _, _, new ->
        ThemeConfigUtils.getInstance(context).notifyThemeConfigChanged()
    }
        private set

    private var ambientLightCriterion: AmbientLightCriterion? = null
    val supportsAmbientLight
        get() = ambientLightCriterion != null


    init {
        PreferenceManager.getDefaultSharedPreferences(context)
                .registerOnSharedPreferenceChangeListener { _, key ->
                    onPreferenceChange(key)
                }

        if (AmbientLightCriterion.isSupported(context))
            ambientLightCriterion = AmbientLightCriterion(context) { updateDarkMode() }
                    .apply { updateMonitoring() }

        updateDarkMode()
    }

    private fun onPreferenceChange(key: String) {
        when (key) {
            Preferences.Theme.DARK_MODE -> {
                ambientLightCriterion?.updateMonitoring()
                updateDarkMode()
            }
            Preferences.Theme.DARK_MODE_AMBIENT_LIGHT -> ambientLightCriterion?.updateMonitoring()
        }
    }


    fun updateDarkMode() {
        val ambientLightActive = Preferences.Theme.darkMode_ambientLight
        val shouldBeActive = when {
            !Preferences.Theme.darkMode -> false
            ambientLightActive -> {
                ((ambientLightActive && (ambientLightCriterion?.shouldEnable ?: false))
            }
            else -> true
        }
        if (isActive == shouldBeActive) return

        AppCompatDelegate.setDefaultNightMode(if (shouldBeActive) MODE_NIGHT_YES else MODE_NIGHT_NO)
        isActive = shouldBeActive
    }


    abstract class Criterion(val shouldEnableChangeListener: () -> Unit) {
        var shouldEnable by Delegates.changeObservable(false) { _, _, _ ->
            shouldEnableChangeListener()
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

    class AmbientLightCriterion(context: Context, shouldEnableChangeListener: () -> Unit) :
            Criterion(shouldEnableChangeListener) {
        companion object {
            const val THRESHOLD = 10f

            fun isSupported(context: Context): Boolean {
                return context.getSystemService<SensorManager>()
                        ?.getDefaultSensor(Sensor.TYPE_LIGHT) != null
            }
        }

        init {
            if (!isSupported(context))
                throw IllegalStateException("AmbientLightCriterion created w/o ambient light sensor")
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
