package org.schulcloud.mobile.ui.settings.changeProfile;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.schulcloud.mobile.data.datamanagers.UserDataManager;
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
        if(!mProfileSubscription.isUnsubscribed())
            mProfileSubscription.unsubscribe();
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
        mProfileSubscription = mUserDataManager.getAccounts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(accounts->{
                    for(AccountResponse account : accounts){
                        if(account.userId == mUserDataManager.getCurrentUserId()){
                            mUserDataManager.saveCurrentAccountId(account._id);
                        }
                    }
                });
    }

    public void changeProfile(@NonNull String firstName, @NonNull String lastName,
                              @NonNull String email, @NonNull String gender,
                              @Nullable String currentPassword,
                              @Nullable String newPassword) {
        CurrentUser currentUser = mUserDataManager.getCurrentUser().toBlocking().value();
        String displayName = currentUser.displayName;
        String userId = currentUser.get_id();
        String accountId = mUserDataManager.getCurrentAccountId();
        String schoolID = currentUser.schoolId;

        AccountRequest accountRequest = new AccountRequest(displayName,newPassword,userId);
        UserRequest userRequest = new UserRequest(accountId,firstName,lastName,email,schoolID,gender);

        mUserDataManager.signIn(currentUser.displayName,currentPassword).doOnError(throwable -> {
            sendToView(v -> v.showPasswordChangeFailed());
            return;
        });

        mProfileSubscription = mUserDataManager.changeProfileInfo(accountRequest,userRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {},
                        throwable ->
                        {Log.e("Profile","OnError",throwable);
                         sendToView(v -> v.showProfileError());},
                        () -> sendToView(v -> v.showChangeSuccess()));
    }

    public CurrentUser getCurrentUser(){
        return mUserDataManager.getCurrentUser().toBlocking().value();
    }
}
