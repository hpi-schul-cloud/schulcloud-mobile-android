package org.schulcloud.mobile

import androidx.multidex.MultiDexApplication

/**
* Application that avoids Realm usage for testing with Robolectric
 */
class SchulCloudTestApp : MultiDexApplication() {
    companion object {
        val TAG: String = SchulCloudTestApp::class.java.simpleName
        lateinit var instance: SchulCloudTestApp
    }

    init {
        instance = this
    }
}
