package org.schulcloud.mobile

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
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
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onCreate() {
        Fabric.with(this, Crashlytics())

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
