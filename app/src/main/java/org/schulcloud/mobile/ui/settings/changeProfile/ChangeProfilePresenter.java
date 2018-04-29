package org.schulcloud.mobile.ui.settings.changeProfile;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.model.CurrentAccount;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.requestBodies.AccountRequest;
import org.schulcloud.mobile.data.model.requestBodies.UserRequest;
import org.schulcloud.mobile.data.model.responseBodies.AccountResponse;
import org.schulcloud.mobile.ui.base.BasePresenter;

import java.util.List;
import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class ChangeProfilePresenter extends BasePresenter<ChangeProfileMvpView>{

    private final UserDataManager mUserDataManager;

    private Subscription mDemoModeSubscription;
    private Subscription mEventsSubscription;
    private Subscription mDevicesSubscription;
    private Subscription mProfileSubscription;
    private Subscription mAccountSubscription;
    private Subscription mSignInSubscription;

    @Inject
    public ChangeProfilePresenter(UserDataManager userDataManager) {
        mUserDataManager = userDataManager;
    }

    @Override
    public void onViewAttached(@NonNull ChangeProfileMvpView view) {
        super.onViewAttached(view);
    }

    /* Profile */
    public void loadProfile()
    {
        syncCurrentAccount();
        mProfileSubscription = mUserDataManager.getCurrentUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        currentUser -> {
                            sendToView(v -> v.showProfile(currentUser));
                        },
                        throwable -> {
                            Timber.e(throwable, "An error occured while loading the profile"
                            );
                            sendToView(v -> v.showProfileError());
                        });
    }

    public void syncCurrentAccount(){
        mAccountSubscription = mUserDataManager.getAccount()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(accounts->{
                    mUserDataManager.saveCurrentAccountId(accounts.get(0)._id);
                    mUserDataManager.saveCurrentAccountName(accounts.get(0).username);
                });
    }

    public void changeProfile(@NonNull String firstName, @NonNull String lastName,
                              @NonNull String email, @NonNull String gender,
                              @Nullable String currentPassword,
                              @Nullable String newPassword) {
        CurrentUser currentUser = mUserDataManager.getCurrentUser().toBlocking().value();
        String displayName = mUserDataManager.getCurrentAccountName();
        String userId = currentUser.get_id();
        String accountId = mUserDataManager.getCurrentAccountId();
        String schoolID = currentUser.schoolId;


        AccountRequest accountRequest = new AccountRequest(displayName,newPassword,accountId,currentPassword);
        UserRequest userRequest = new UserRequest(userId,firstName,lastName,email,schoolID,gender);

        boolean loginBad = false;

        if(!newPassword.equals(""))
            mProfileSubscription = mUserDataManager.changeProfileInfo(accountRequest,userRequest)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(userResponse -> {sendToView(v -> v.finishChange());},
                            throwable ->
                            {Log.e("Profile","OnError",throwable);
                                sendToView(v -> v.showProfileChangeFailed());},
                            () -> {});
        else
            mProfileSubscription = mUserDataManager.changeUserInfo(userRequest)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(userResponse -> {sendToView(v -> v.finishChange());},
                            throwable ->
                            {Log.e("Profile","OnError",throwable);
                                sendToView(v -> v.showPasswordChangeFailed());},
                            () -> {});

    }

    public CurrentUser getCurrentUser(){
        return mUserDataManager.getCurrentUser().toBlocking().value();
    }
}
