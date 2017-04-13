package org.schulcloud.mobile.ui.signin;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.AccessToken;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.ui.base.BasePresenter;
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

    public void signIn() {
        checkViewAttached();

        mSubscription = mDataManager.signIn("schueler@schul-cloud.org", "schulcloud")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AccessToken>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showSignInSuccessful();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading the users.");
                        getMvpView().showSignInFailed();
                    }

                    @Override
                    public void onNext(AccessToken accessToken) {
                        if (accessToken == null) {
                            getMvpView().showSignInFailed();
                        } else {
                            getMvpView().showSignInSuccessful();
                        }
                    }
                });
    }
}
