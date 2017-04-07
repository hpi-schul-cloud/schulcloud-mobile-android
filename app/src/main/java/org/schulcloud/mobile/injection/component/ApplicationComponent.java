package org.schulcloud.mobile.injection.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.SyncService;
import org.schulcloud.mobile.data.local.DatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.remote.RibotsService;
import org.schulcloud.mobile.injection.ApplicationContext;
import org.schulcloud.mobile.injection.module.ApplicationModule;
import org.schulcloud.mobile.util.RxEventBus;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(SyncService syncService);

    @ApplicationContext Context context();
    Application application();
    RibotsService ribotsService();
    PreferencesHelper preferencesHelper();
    DatabaseHelper databaseHelper();
    DataManager dataManager();
    RxEventBus eventBus();

}
