package org.schulcloud.mobile.ui.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

public interface MainMvpView<V> extends MvpView {

    int getTabCount();
    Pair<Integer, V> createInitialView(int tabIndex);

    void showView(int oldViewId, int newViewId, @Nullable V newView, int oldTabIndex,
            int newTabIndex);
    void removeViews(@NonNull List<Integer> viewIds);

    boolean currentViewHandlesBack(int viewId);

    void finish();

}
