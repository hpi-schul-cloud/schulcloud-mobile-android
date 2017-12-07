package org.schulcloud.mobile.ui.base;

import android.support.annotation.NonNull;

/**
 * Every presenter in the app must either implement this interface or extend BasePresenter
 * indicating the MvpView type that wants to be attached with.
 */
public interface Presenter<V extends MvpView> {

    void attachView(@NonNull V mvpView);

    void detachView();

}
