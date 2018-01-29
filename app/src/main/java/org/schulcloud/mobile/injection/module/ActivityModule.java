package org.schulcloud.mobile.injection.module;

import android.app.Activity;
import android.content.Context;

import org.schulcloud.mobile.injection.ActivityContext;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.ui.base.MvpView;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private BaseActivity<? extends MvpView, ? extends BasePresenter> mActivity;

    public ActivityModule(BaseActivity<? extends MvpView, ? extends BasePresenter> activity) {
        mActivity = activity;
    }

    @Provides
    Activity provideActivity() {
        return mActivity;
    }
    @Provides
    BaseActivity<? extends MvpView, ? extends BasePresenter> provideBaseActivity() {
        return mActivity;
    }

    @Provides
    @ActivityContext
    Context providesContext() {
        return mActivity;
    }
}
