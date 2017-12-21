package org.schulcloud.mobile.ui.signin;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class SignInPresenter extends BasePresenter<SignInMvpView> {

    @Inject
    public SignInPresenter(UserDataManager userDataManager) {
        mUserDataManager = userDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSubscription);
    }

    public void signIn(@NonNull String username, @NonNull String password, boolean demoMode) {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mUserDataManager.signIn(username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> mUserDataManager.setInDemoMode(demoMode))
                .subscribe(
                        accessToken -> {},
                        throwable -> {
                            Timber.e(throwable, "There was an error signing in.");
                            getMvpView().showSignInFailed();
                        },
                        () -> getMvpView().showSignInSuccessful());
    }
}
