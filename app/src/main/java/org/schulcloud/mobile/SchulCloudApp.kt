package org.schulcloud.mobile

import androidx.multidex.MultiDexApplication
import io.realm.Realm
import io.realm.RealmConfiguration
import net.danlew.android.joda.JodaTimeAndroid
import org.schulcloud.mobile.config.Config
import org.schulcloud.mobile.utils.ThemeConfigUtils

class SchulCloudApp : MultiDexApplication() {
    companion object {
        val TAG: String = SchulCloudApp::class.java.simpleName
        lateinit var instance: SchulCloudApp
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        configureRealm()
        JodaTimeAndroid.init(this)

        ThemeConfigUtils.getInstance(this).startMonitoring()
    }

    private fun configureRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .schemaVersion(Config.REALM_SCHEMA_VERSION)
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)
    }
}
