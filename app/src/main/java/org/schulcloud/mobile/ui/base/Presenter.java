package org.schulcloud.mobile.ui.base;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.util.Action;

/**
 * Every presenter in the app must either implement this interface or extend BasePresenter
 * indicating the MvpView type that wants to be attached with.
 */
public interface Presenter<V extends MvpView> {

    void onViewAttached(@NonNull V view);
    void onViewDetached();
    void onDestroy();

    void attachView(@NonNull V view);
    void detachView();
    void destroy();

    boolean isViewAttached();
    @NonNull
    V getViewOrThrow();

    void sendToView(@NonNull Action<V> action);

}
