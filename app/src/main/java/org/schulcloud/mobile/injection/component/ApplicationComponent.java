package org.schulcloud.mobile.injection.component;

import android.app.Application;
import android.content.Context;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.local.DatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.remote.RestService;
import org.schulcloud.mobile.data.sync.DirectorySyncService;
import org.schulcloud.mobile.data.sync.FileSyncService;
import org.schulcloud.mobile.data.sync.UserSyncService;
import org.schulcloud.mobile.injection.ApplicationContext;
import org.schulcloud.mobile.injection.module.ApplicationModule;
import org.schulcloud.mobile.injection.module.RestModule;
import org.schulcloud.mobile.util.RxEventBus;

import javax.inject.Singleton;

import dagger.Component;
import io.realm.Realm;

@Singleton
@Component(modules = {ApplicationModule.class, RestModule.class})
public interface ApplicationComponent {

    void inject(UserSyncService userSyncService);
    void inject(FileSyncService fileSyncService);
    void inject(DirectorySyncService directorySyncService);


    @ApplicationContext Context context();
    Application application();
    RestService restService();
    PreferencesHelper preferencesHelper();
    DatabaseHelper databaseHelper();
    Realm realm();
    DataManager dataManager();
    RxEventBus eventBus();
}
