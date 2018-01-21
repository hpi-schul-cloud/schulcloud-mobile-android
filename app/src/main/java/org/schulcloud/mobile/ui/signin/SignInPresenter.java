package org.schulcloud.mobile.ui.signin;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class SignInPresenter extends BasePresenter<SignInMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public SignInPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtil.unsubscribe(mSubscription);
    }

    public void signIn(@NonNull String username, @NonNull String password, boolean demoMode) {
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.signIn(username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> mDataManager.setInDemoMode(demoMode))
                .subscribe(
                        accessToken -> {},
                        throwable -> {
                            Timber.e(throwable, "There was an error signing in.");
                            sendToView(SignInMvpView::showSignInFailed);
                        },
                        () -> sendToView(SignInMvpView::showSignInSuccessful));
    }

    public void sendPasswordRecovery(String username) {
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.sendPasswordRecovery(username)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {},
                        throwable -> {
                            Timber.e(throwable,"There was an error while sending the passwordRecovery.");
                            sendToView(SignInMvpView::showPasswordRecoveryFailed);
                        }
                        , () -> sendToView(SignInMvpView::showPasswordRecovery));
    }
}
