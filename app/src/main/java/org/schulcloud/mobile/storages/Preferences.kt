package org.schulcloud.mobile.storages

import org.schulcloud.mobile.SchulCloudApp
import org.schulcloud.mobile.storages.base.BaseStorage

private val NAME = SchulCloudApp.instance.packageName + "_preferences"

object Preferences : BaseStorage(NAME) {
    object Theme : BaseStorage(NAME) {
        const val DARK_MODE = "theme_darkMode"
        var darkMode by BooleanPreference(DARK_MODE)

        const val DARK_MODE_AMBIENT_LIGHT = "theme_darkMode_ambientLight"
        var darkMode_ambientLight by BooleanPreference(DARK_MODE_AMBIENT_LIGHT)
    }
}
