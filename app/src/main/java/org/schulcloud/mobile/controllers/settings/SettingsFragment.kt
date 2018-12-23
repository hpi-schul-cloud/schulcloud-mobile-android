package org.schulcloud.mobile.controllers.settings

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.storages.Preferences
import org.schulcloud.mobile.utils.DarkModeUtils
import org.schulcloud.mobile.utils.hasPermission

class SettingsFragment : PreferenceFragmentCompat() {
    companion object {
        val TAG = SettingsFragment::class.simpleName!!
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val context = context ?: return
        // Check HW support for ambient light sensor
        if (DarkModeUtils.getInstance(context).supportsAmbientLight)
            findPreference<Preference>(Preferences.Theme.DARK_MODE_AMBIENT_LIGHT).isVisible = true
        // Request location permission for accurate sunrise/sunset values
        val theme_darkMode_night = findPreference<CheckBoxPreference>(Preferences.Theme.DARK_MODE_NIGHT)
        theme_darkMode_night.summaryProvider = Preference.SummaryProvider<CheckBoxPreference> {
            if (Preferences.Theme.darkMode_night && !context.hasPermission(ACCESS_COARSE_LOCATION))
                getString(R.string.settings_theme_darkMode_night_summary_noLocationPermission)
            else null
        }
        theme_darkMode_night.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == true && !context.hasPermission(ACCESS_COARSE_LOCATION))
                launch {
                    if ((activity as BaseActivity).requestPermission(ACCESS_COARSE_LOCATION))
                        DarkModeUtils.getInstance(context).onLocationPermissionGranted()
                }

            return@setOnPreferenceChangeListener true
        }
        // Check support for power saver
        if (DarkModeUtils.getInstance(context).supportsPowerSaver)
            findPreference<Preference>(Preferences.Theme.DARK_MODE_POWER_SAVER).isVisible = true
    }
}
