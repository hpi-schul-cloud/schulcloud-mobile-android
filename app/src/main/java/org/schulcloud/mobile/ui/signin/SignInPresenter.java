package org.schulcloud.mobile.ui.signin;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class SignInPresenter extends BasePresenter<SignInMvpView> {

    private final UserDataManager mUserDataManager;
    private Subscription mSubscription;

    @Inject
    public SignInPresenter(UserDataManager userDataManager) {
        mUserDataManager = userDataManager;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtil.unsubscribe(mSubscription);
    }

    public void signIn(@NonNull String username, @NonNull String password, boolean demoMode) {
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mUserDataManager.signIn(username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> mUserDataManager.setInDemoMode(demoMode))
                .subscribe(
                        accessToken -> {},
                        throwable -> {
                            Timber.e(throwable, "There was an error signing in.");
                            sendToView(SignInMvpView::showSignInFailed);
                        },
                        () -> sendToView(SignInMvpView::showSignInSuccessful));
    }
}
