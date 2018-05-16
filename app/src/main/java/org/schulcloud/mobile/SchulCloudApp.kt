package org.schulcloud.mobile

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import org.schulcloud.mobile.config.Config

class SchulCloudApp : Application() {

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