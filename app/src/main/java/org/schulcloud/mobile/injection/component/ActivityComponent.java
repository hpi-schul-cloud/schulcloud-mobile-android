package org.schulcloud.mobile.injection.component;

import org.schulcloud.mobile.injection.scope.PerActivity;
import org.schulcloud.mobile.injection.module.ActivityModule;
import org.schulcloud.mobile.ui.main.MainActivity;
import dagger.Subcomponent;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);
}
