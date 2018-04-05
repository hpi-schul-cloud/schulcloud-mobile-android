package org.schulcloud.mobile.ui.settings.changeProfile;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.schulcloud.mobile.data.datamanagers.EventDataManager;
import org.schulcloud.mobile.data.datamanagers.NotificationDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.model.CurrentAccount;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.requestBodies.AccountRequest;
import org.schulcloud.mobile.data.model.requestBodies.UserRequest;
import org.schulcloud.mobile.ui.base.BasePresenter;

import java.util.regex.Pattern;

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

    public void changeProfile(@NonNull String firstName, @NonNull String lastName,
                              @NonNull String email, @NonNull String gender,
                              @Nullable String currentPassword,
                              @Nullable String newPassword, @Nullable String newPasswordRepeat) {
        if(newPassword.length() < 8 || newPassword.equals(newPassword.toLowerCase()) || newPassword
                .equals(newPassword.toUpperCase()) || Pattern.matches("[a-zA-Z]+",newPassword)){
            sendToView(v -> v.showPasswordBad());
        }
        if(newPassword.equals("") || newPassword.equals(currentPassword) || !(newPassword.equals(newPasswordRepeat))) {
            sendToView(v -> v.showPasswordChangeFailed());
            return;
        }

        CurrentUser currentUser = mUserDataManager.getCurrentUser().toBlocking().value();
        CurrentAccount currentAccount = mUserDataManager.getCurrentAccount().toBlocking().value();
        String displayName = currentUser.displayName;
        String userId = currentUser.get_id();
        String accountId = currentAccount.accountId;
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
