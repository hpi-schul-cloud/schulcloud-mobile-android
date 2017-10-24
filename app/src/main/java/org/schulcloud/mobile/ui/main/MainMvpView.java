package org.schulcloud.mobile.ui.main;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.ui.base.MvpView;

public interface MainMvpView extends MvpView {

    void showFragment(@NonNull MainFragment fragment, int tab);

    void finish();

}
