package org.schulcloud.mobile.ui.signin.passwordRecovery;

import android.provider.ContactsContract;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.ui.signin.SignInMvpView;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class PasswordRecoveryPresenter extends BasePresenter<PasswordRecoveryMvpView> {

    DataManager mDataManager;

    @Inject
    public PasswordRecoveryPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void onViewDetached(){
        super.onViewDetached();
    }

    public void sendPasswordRecovery(String username)
    {

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
