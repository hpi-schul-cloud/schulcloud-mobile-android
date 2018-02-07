package org.schulcloud.mobile.test.common.injection.component;

import javax.inject.Singleton;

import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.injection.component.ApplicationComponent;
import org.schulcloud.mobile.test.common.injection.module.ApplicationTestModule;
import dagger.Component;

@Singleton
@Component(modules = ApplicationTestModule.class)
public interface TestComponent extends ApplicationComponent {
    UserDataManager userDataManager();
}
