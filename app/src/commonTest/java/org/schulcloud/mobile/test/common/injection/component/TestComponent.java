package org.schulcloud.mobile.test.common.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import org.schulcloud.mobile.injection.component.ApplicationComponent;
import org.schulcloud.mobile.test.common.injection.module.ApplicationTestModule;

@Singleton
@Component(modules = ApplicationTestModule.class)
public interface TestComponent extends ApplicationComponent {

}
