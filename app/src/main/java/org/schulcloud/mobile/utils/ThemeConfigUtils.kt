package org.schulcloud.mobile.utils

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.text.format.DateUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
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

    fun startMonitoring() {
        DarkModeUtils.getInstance(context).startMonitoring()
    }


    fun addThemeChangeListener(listener: ThemeConfigChangeListener) {
        themeChangeListeners.add(listener)
        DarkModeUtils.getInstance(context).onThemeConfigListenerCountChanged(themeChangeListeners.size)
    }

    fun removeThemeChangeListener(listener: ThemeConfigChangeListener) {
        themeChangeListeners.remove(listener)
        DarkModeUtils.getInstance(context).onThemeConfigListenerCountChanged(themeChangeListeners.size)
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

    var isActive by Delegates.changeObservable(false) { _, _, _ ->
        ThemeConfigUtils.getInstance(context).notifyThemeConfigChanged()
    }
        private set

    private val onPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        onPreferenceChange(key)
    }

    private var ambientLightCriterion: AmbientLightCriterion? = null
    val supportsAmbientLight
        get() = ambientLightCriterion != null
    private var nightCriterion = NightCriterion(context) { updateDarkMode() }
    private val criteria = mutableListOf<Criterion>(nightCriterion)


    init {
        PreferenceManager.getDefaultSharedPreferences(context)
                .registerOnSharedPreferenceChangeListener(onPreferenceChangeListener)

        ambientLightCriterion = AmbientLightCriterion.createIfSupported(context) { updateDarkMode() }
                ?.apply {
                    updateMonitoring()
                    criteria.add(this)
                }
        nightCriterion.updateMonitoring()
    }

    fun startMonitoring() {
        updateDarkMode()
    }

    private fun onPreferenceChange(key: String) {
        when (key) {
            Preferences.Theme.DARK_MODE -> {
                ambientLightCriterion?.updateMonitoring()
                nightCriterion.updateMonitoring()
            }
            Preferences.Theme.DARK_MODE_AMBIENT_LIGHT -> ambientLightCriterion?.updateMonitoring()
            Preferences.Theme.DARK_MODE_NIGHT -> nightCriterion.updateMonitoring()
        }
        updateDarkMode()
    }

    private fun updateDarkMode() {
        val ambientLightActive = Preferences.Theme.darkMode_ambientLight
        val nightActive = Preferences.Theme.darkMode_night
        val shouldBeActive = when {
            !Preferences.Theme.darkMode -> false
            ambientLightActive || nightActive -> {
                ((ambientLightActive && (ambientLightCriterion?.shouldEnable ?: false))
                        || (nightActive && nightCriterion.shouldEnable))
            }
            else -> true
        }
        if (isActive == shouldBeActive) return

        AppCompatDelegate.setDefaultNightMode(if (shouldBeActive) MODE_NIGHT_YES else MODE_NIGHT_NO)
        isActive = shouldBeActive
    }


    fun onThemeConfigListenerCountChanged(count: Int) {
        if (count == 0) criteria.forEach { it.stopMonitoring() }
        else criteria.forEach { it.updateMonitoring() }
    }

    fun onLocationPermissionGranted() {
        nightCriterion.apply {
            stopMonitoring()
            updateMonitoring()
        }
    }


    abstract class Criterion(val shouldEnableChangeListener: ShouldEnableChangeListener) {
        var shouldEnable by Delegates.changeObservable(false) { _, _, _ ->
            shouldEnableChangeListener()
        }
            protected set

        var isMonitoring = false
            private set

        fun updateMonitoring() {
            val shouldMonitor = Preferences.Theme.darkMode && shouldMonitor()
            if (isMonitoring == shouldMonitor) return

            if (shouldMonitor) startMonitoring()
            else {
                stopMonitoring()
                shouldEnable = false
            }
        }

        protected abstract fun shouldMonitor(): Boolean
        fun startMonitoring() {
            if (isMonitoring) return

            onStartMonitoring()
            isMonitoring = true
        }

        protected abstract fun onStartMonitoring()
        fun stopMonitoring() {
            if (!isMonitoring) return

            onStopMonitoring()
            isMonitoring = false
        }

        protected abstract fun onStopMonitoring()
    }

    class AmbientLightCriterion private constructor(
        context: Context,
        shouldEnableChangeListener: ShouldEnableChangeListener
    ) : Criterion(shouldEnableChangeListener) {
        companion object {
            const val THRESHOLD = 10f

            fun createIfSupported(
                context: Context,
                shouldEnableChangeListener: ShouldEnableChangeListener
            ): AmbientLightCriterion? {
                return if (context.getSystemService<SensorManager>()
                                ?.getDefaultSensor(Sensor.TYPE_LIGHT) != null)
                    AmbientLightCriterion(context, shouldEnableChangeListener)
                else {
                    Preferences.Theme.darkMode_ambientLight = false
                    null
                }
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

        override fun onStartMonitoring() {
            sensorManager.registerListener(eventListener, sensor, SensorManager.SENSOR_DELAY_UI)
        }

        override fun onStopMonitoring() {
            sensorManager.unregisterListener(eventListener)
        }
    }

    class NightCriterion(val context: Context, shouldEnableChangeListener: () -> Unit) :
            Criterion(shouldEnableChangeListener) {
        private var timer: Timer? = null


        override fun shouldMonitor() = Preferences.Theme.darkMode_night

        override fun onStartMonitoring() {
            timer = fixedRateTimer("theme-darkMode-night", true, period = DateUtils.MINUTE_IN_MILLIS) {
                val isNight = TwilightManager.getInstance(context).isNight()
                if (shouldEnable != isNight)
                    launch(UI) {
                        shouldEnable = isNight
                    }
            }
        }

        override fun onStopMonitoring() {
            timer?.cancel()
            timer = null
        }
    }
}

typealias ShouldEnableChangeListener = () -> Unit
