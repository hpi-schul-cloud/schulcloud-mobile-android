package org.schulcloud.mobile.ui.signin;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.AccessToken;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.ui.main.MainMvpView;
import org.schulcloud.mobile.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class SignInPresenter extends BasePresenter<SignInMvpView> {
    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public SignInPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(SignInMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void signIn(String username, String password) {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);

        // todo: remove credentials
        username = username.equals("") ? "schueler@schul-cloud.org" : username;
        password = password.equals("") ? "schulcloud" : password;

        mSubscription = mDataManager.signIn(username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    // onNext
                    accessToken -> {
                        if (accessToken == null) {
                            getMvpView().showSignInFailed();
                        } else {
                            getMvpView().showSignInSuccessful();
                        }
                    },
                    // onError
                    error -> {
                        Timber.e(error, "There was an error loading the users.");
                        getMvpView().showSignInFailed();
                    },
                    // onCompleted
                    () -> getMvpView().showSignInSuccessful());
    }
}
