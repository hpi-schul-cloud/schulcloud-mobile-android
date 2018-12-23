package org.schulcloud.mobile.controllers.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.schulcloud.mobile.R
import org.schulcloud.mobile.storages.Preferences
import org.schulcloud.mobile.utils.DarkModeUtils

class SettingsFragment : PreferenceFragmentCompat() {
    companion object {
        val TAG = SettingsFragment::class.simpleName!!
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val context = context ?: return
        if (DarkModeUtils.getInstance(context).supportsAmbientLight)
            findPreference<Preference>(Preferences.Theme.DARK_MODE_AMBIENT_LIGHT).isVisible = true
    }
}
